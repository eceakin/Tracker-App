// Top-level build file where you can add configuration options common to all sub-projects/modules.
import javax.xml.parsers.DocumentBuilderFactory
import org.gradle.api.artifacts.ProjectDependency
import org.w3c.dom.Element

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

// K-01 — motor sürümü eklenti sürümünden bağımsızdır; ikisi de catalog'dan gelir.
val ktlintEngineVersion = libs.versions.ktlintEngine.get()

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set(ktlintEngineVersion)
    }
}

// Included build'lerin görevleri kendiliğinden çalışmaz; build-logic'i de aynı komutla denetle.
tasks.named("ktlintCheck") {
    dependsOn(gradle.includedBuild("build-logic").task(":convention:ktlintCheck"))
}

// M-08 — mimari-ve-kodlama-ilkeleri.md Bölüm 1'deki izin tablosu. Tabloya modül ya da izin
// eklemek mimari bir karardır: önce belge, sonra bu harita değişir. `null` "hepsi" demektir.
val m08AllowedDependencies: Map<String, Set<String>?> = mapOf(
    ":core:domain" to emptySet(),
    ":core:data" to setOf(":core:domain"),
    ":core:prayertimes" to setOf(":core:domain"),
    ":feature:*" to setOf(":core:domain"),
    ":widget" to setOf(":core:domain", ":core:data"),
    ":app" to null
)

fun Project.hasBuildScript(): Boolean =
    this != rootProject && layout.projectDirectory.file("build.gradle.kts").asFile.exists()

fun m08AllowedTargets(modulePath: String): Set<String>? {
    val rule = m08AllowedDependencies.keys.firstOrNull { pattern ->
        pattern == modulePath || patternIsMatch(pattern, modulePath)
    } ?: throw GradleException(
        "M-08 ihlali: $modulePath modülü M-08 izin tablosunda tanımlı değil. " +
            "Modülü kök build.gradle.kts'teki tabloya ekle."
    )
    return m08AllowedDependencies[rule]
}

fun patternIsMatch(pattern: String, modulePath: String): Boolean =
    pattern.endsWith(":*") && modulePath.startsWith(pattern.removeSuffix("*"))

fun checkM08Dependency(
    modulePath: String,
    configurationName: String,
    targetPath: String,
    allowedTargets: Set<String>
) {
    // AGP, androidTest yapılandırmalarına modülün kendisini ekler.
    if (targetPath == modulePath || targetPath in allowedTargets) return
    throw GradleException(
        "M-08 ihlali: $modulePath → $targetPath (yapılandırma: $configurationName). " +
            "İzin verilenler: ${allowedTargets.sorted()}"
    )
}

gradle.projectsEvaluated {
    val modules = allprojects.filter { it.hasBuildScript() }
    val modulePaths = modules.map { it.path }.toSet()

    modules.forEach { module ->
        val allowedTargets = m08AllowedTargets(module.path) ?: (modulePaths - module.path)
        module.configurations.forEach { configuration ->
            val projectDependencies =
                configuration.dependencies.withType(ProjectDependency::class.java)
            projectDependencies.forEach { dependency ->
                checkM08Dependency(
                    module.path,
                    configuration.name,
                    dependency.path,
                    allowedTargets
                )
            }
        }
    }
}

// K-01 — mimari-ve-kodlama-ilkeleri.md §7'deki bilinen lint uyarıları tablosu.
// Liste değişikliği planlama kararıdır: önce belge, sonra bu harita.
val k01KnownLintWarnings: Map<Pair<String, String>, Int> = mapOf(
    ("ObsoleteLintCustomCheck" to ":feature:calendar") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:kaza") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:mazeret") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:onboarding") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:settings") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:today") to 1
)

// K-01 1.5 — sürüm güncelliği denetimleri internetteki güncel sürüme bakar; sonuçları
// zamana ve ağa bağlıdır. Kapı bunları karşılaştırmaz, ama lint'te kapatmaz ve gizlemez.
val k01OutOfScopeLintIds: Set<String> = setOf(
    "GradleDependency",
    "NewerVersionAvailable",
    "AndroidGradlePluginVersion"
)

fun lintReportPath(module: Project): File =
    module.layout.projectDirectory.file("build/reports/lint-results-debug.xml").asFile

fun displayFilePath(root: File, path: String): String {
    val file = File(path).toPath()
    val rootPath = root.toPath()
    return if (file.isAbsolute && file.startsWith(rootPath)) {
        rootPath.relativize(file).toString()
    } else {
        path
    }
}

val checkLintWarnings = tasks.register("checkLintWarnings") {
    group = "verification"
    description = "Android Lint uyarılarını mimari K-01 bilinen uyarılar listesiyle karşılaştırır."
    doLast {
        val diffs = mutableListOf<String>()
        val actualCounts = mutableMapOf<Pair<String, String>, Int>()
        val firstLocations = mutableMapOf<Pair<String, String>, String>()
        val missingReports = mutableSetOf<String>()
        val lintModules = allprojects
            .filter { it.tasks.names.contains("lintDebug") }
            .sortedBy { it.path }

        lintModules.forEach { module ->
            val report = lintReportPath(module)
            if (!report.isFile) {
                missingReports += module.path
                diffs += "Lint kapısı: rapor yok (${module.path}) " +
                    displayFilePath(rootDir, report.path) + ". Lint çalışmamış olabilir."
                return@forEach
            }

            val document = try {
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(report)
            } catch (exception: Exception) {
                diffs += "Lint kapısı: rapor okunamadı (${module.path}) " +
                    displayFilePath(rootDir, report.path) + ": " + exception.message
                return@forEach
            }

            val issues = document.getElementsByTagName("issue")
            for (index in 0 until issues.length) {
                val issue = issues.item(index) as Element
                val id = issue.getAttribute("id")
                if (id in k01OutOfScopeLintIds) continue
                if (issue.getAttribute("severity") != "Warning") continue

                val key = id to module.path
                actualCounts[key] = (actualCounts[key] ?: 0) + 1

                val issueLocations = issue.getElementsByTagName("location")
                if (issueLocations.length > 0) {
                    val location = issueLocations.item(0) as Element
                    val file = location.getAttribute("file")
                    if (file.isNotEmpty()) {
                        val line = location.getAttribute("line")
                        val display = displayFilePath(rootDir, file)
                        firstLocations.putIfAbsent(
                            key,
                            if (line.isEmpty()) display else "$display:$line"
                        )
                    }
                }
            }
        }

        actualCounts.forEach { (key, count) ->
            val expected = k01KnownLintWarnings[key] ?: 0
            if (count > expected) {
                val (id, modulePath) = key
                val location = firstLocations[key]?.let { " $it" } ?: ""
                val kind = if (expected == 0) "yeni uyarı" else "fazla uyarı"
                diffs += "Lint kapısı: $kind $id ($modulePath)$location. " +
                    "Uyarıyı düzelt; bilinçli bir istisnaysa mimari K-01 tablosuna " +
                    "eklenmesi için planlamaya sor."
            }
        }

        k01KnownLintWarnings.forEach { (key, expected) ->
            val (id, modulePath) = key
            if (modulePath in missingReports) return@forEach
            val actual = actualCounts[key] ?: 0
            if (actual < expected) {
                diffs += "Lint kapısı: bilinen uyarı $id ($modulePath) artık görünmüyor. " +
                    "Mimari K-01 tablosundan ve bu listeden çıkarılması için planlamaya bildir."
            }
        }

        if (diffs.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("Lint kapısı başarısız: ${diffs.size} fark bulundu.")
                    diffs.sorted().forEach { appendLine("- $it") }
                }
            )
        }

        logger.lifecycle(
            "Lint kapısı: bilinen ${k01KnownLintWarnings.values.sum()} uyarı eşleşti, fark yok."
        )
    }
}

gradle.projectsEvaluated {
    allprojects
        .filter { it.tasks.names.contains("lintDebug") }
        .forEach { module ->
            checkLintWarnings.configure { dependsOn("${module.path}:lintDebug") }
        }
}

// K-16 — bekleyen KT listesi. Bir Faz 2 kartı kapsadığı kimlikleri buradan siler.
// Listeye kimlik eklemek ya da kartın söylemediği bir kimliği silmek planlama kararıdır.
val ktPending: Set<String> = setOf(
    // F2-04b
    "KT-12",
    "KT-13",
    // F2-05
    "KT-19",
    "KT-20",
    "KT-21",
    "KT-22",
    "KT-23",
    "KT-24",
    "KT-25",
    "KT-26",
    "KT-27",
    "KT-32",
    "KT-33",
    "KT-91",
    // F2-06
    "KT-28",
    "KT-29",
    "KT-30",
    "KT-31",
    "KT-82",
    "KT-83",
    "KT-84",
    "KT-92",
    "KT-93",
    // F2-07
    "KT-37",
    "KT-38",
    "KT-39",
    "KT-40",
    "KT-41",
    "KT-42",
    "KT-43",
    "KT-44",
    "KT-45",
    "KT-46",
    "KT-47",
    "KT-48",
    "KT-49",
    "KT-50",
    "KT-51",
    "KT-90",
    // F2-08
    "KT-52",
    "KT-53",
    "KT-54",
    "KT-55",
    "KT-56",
    "KT-57",
    "KT-58",
    "KT-59",
    "KT-60",
    "KT-61",
    "KT-80",
    "KT-87",
    "KT-88",
    "KT-89",
    "KT-94",
    // F2-09
    "KT-62",
    "KT-63",
    "KT-95",
    "KT-96",
    "KT-97",
    "KT-98",
    "KT-101",
    "KT-102",
    // F2-10
    "KT-64",
    "KT-65",
    "KT-66",
    "KT-67",
    "KT-68",
    // F2-11
    "KT-69",
    "KT-70",
    // F2-12
    "KT-78",
    "KT-79",
    "KT-85",
    "KT-86",
    "KT-100",
    // F2-13
    "KT-71",
    "KT-72",
    "KT-73",
    "KT-74",
    "KT-75",
    "KT-76",
    "KT-77",
    "KT-99"
)

val checkKtTraceability = tasks.register("checkKtTraceability") {
    group = "verification"
    description =
        "Karar tablosundaki KT satırlarını :core:domain testlerindeki KT kimlikleriyle iki yönlü karşılaştırır."
    doLast {
        val tableFile = rootDir.resolve("docs/karar-tablosu.md")
        val tableText = try {
            tableFile.readText(Charsets.UTF_8)
        } catch (exception: Exception) {
            val reason = exception.message ?: exception.javaClass.name
            throw GradleException(
                "KT kapısı: docs/karar-tablosu.md okunamadı: $reason."
            )
        }

        // D2 — kimlikler yakalandığı gibi kullanılır; sayıya çevirme ya da sıfırla
        // doldurma yoktur. Böylece KT001_ gibi yanlış bir yazım KT-001 olur ve geçersiz sayılır.
        val rowRegex = Regex("""^\|\s*KT-(\d+)\s*\|""")
        val validIds = mutableSetOf<String>()
        tableText.lineSequence().forEach { rawLine ->
            val line = rawLine.trimEnd('\r')
            val match = rowRegex.find(line) ?: return@forEach
            val cells = line.split("|").map { it.trim() }
            if (cells.any { it.startsWith("*Kaldırıldı") }) return@forEach
            validIds += "KT-" + match.groupValues[1]
        }
        if (validIds.isEmpty()) {
            throw GradleException(
                "KT kapısı: docs/karar-tablosu.md içinde KT satırı bulunamadı."
            )
        }

        val functionRegex = Regex("""\bfun\s+KT(\d{2,3})_""")
        val argumentSetRegex = Regex("""argumentSet\(\s*"KT-(\d{2,3})(?!\d)""")
        // D3 — satır başı şartı yok; @Disabled herhangi bir yerde geçebilir.
        val disabledRegex = Regex("""@(?:org\.junit\.jupiter\.api\.)?Disabled\w*""")

        val testedIds = mutableSetOf<String>()
        val diffs = mutableListOf<String>()
        val testRoot = rootDir.resolve("core/domain/src/test/kotlin")
        testRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .sortedBy { it.path }
            .forEach { file ->
                val relativePath = file.relativeTo(rootDir).invariantSeparatorsPath
                file.readText(Charsets.UTF_8)
                    .lineSequence()
                    .map { it.trimEnd('\r') }
                    .forEachIndexed { index, line ->
                        functionRegex.findAll(line).forEach { match ->
                            testedIds += "KT-" + match.groupValues[1]
                        }
                        argumentSetRegex.findAll(line).forEach { match ->
                            testedIds += "KT-" + match.groupValues[1]
                        }
                        if (disabledRegex.containsMatchIn(line)) {
                            diffs += "KT kapısı: @Disabled bulundu: $relativePath:${index + 1}. " +
                                "Testler devre dışı bırakılamaz (CLAUDE.md Bölüm 8)."
                        }
                    }
            }

        (ktPending + testedIds)
            .filter { it !in validIds }
            .sorted()
            .forEach { id ->
                diffs += "KT kapısı: $id karar tablosunda geçerli bir satır değil " +
                    "(kaldırılmış ya da yok). Planlamaya bildir."
            }

        validIds
            .filter { it !in testedIds && it !in ktPending }
            .sorted()
            .forEach { id ->
                diffs += "KT kapısı: $id için test yok ve bekleyen listede de değil. " +
                    "Testini yaz ya da planlamaya sor."
            }

        testedIds
            .filter { it in validIds && it in ktPending }
            .sorted()
            .forEach { id ->
                diffs += "KT kapısı: $id kimliğinin testi var ama bekleyen listede duruyor. " +
                    "Kimliği ktPending'den sil."
            }

        if (diffs.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("KT kapısı başarısız: ${diffs.size} fark bulundu.")
                    diffs.sorted().forEach { appendLine("- $it") }
                }
            )
        }

        logger.lifecycle(
            "KT izlenebilirlik: ${validIds.size} geçerli satır, " +
                "${testedIds.size} testli, ${ktPending.size} bekliyor, fark yok."
        )
    }
}

// CI'ın çalıştırdığı tek komut. Kök görevde `dependsOn("ktlintCheck")` yalnızca kökün
// görevine bağlanır; bu yüzden her projenin görevine açıkça bağlanıyor.
val ciCheck = tasks.register("ciCheck") {
    group = "verification"
    description =
        "ktlint, birim testleri, kapsam kapısı, KT izlenebilirlik kapısı, " +
        "Android Lint kapısı ve :app debug derlemesini çalıştırır."
    dependsOn(checkLintWarnings, checkKtTraceability)
}

gradle.projectsEvaluated {
    allprojects.forEach { module ->
        if (module.tasks.names.contains("ktlintCheck")) {
            ciCheck.configure { dependsOn(module.tasks.named("ktlintCheck")) }
        }
        if (module != rootProject && module.tasks.names.contains("test")) {
            ciCheck.configure { dependsOn(module.tasks.named("test")) }
        }
        // K-15 — kapsam kapısı olan her projenin doğrulama görevi ciCheck'e açıkça bağlanır.
        if (module.tasks.names.contains("jacocoTestCoverageVerification")) {
            ciCheck.configure {
                dependsOn(module.tasks.named("jacocoTestCoverageVerification"))
            }
        }
    }
    ciCheck.configure { dependsOn(project(":app").tasks.named("assembleDebug")) }
}
