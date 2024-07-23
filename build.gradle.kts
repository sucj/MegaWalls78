import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev").version("1.7.1")
  id("xyz.jpenilla.run-paper").version("2.3.0")
  id("xyz.jpenilla.resource-factory-bukkit-convention").version("1.1.1")
}

group = "icu.suc.megawalls78"
version = "1.0.0-SNAPSHOT"

java {
  toolchain.languageVersion = JavaLanguageVersion.of(21)
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
  mavenLocal()
  maven("https://repo.papermc.io/repository/maven-public/")
  maven("https://oss.sonatype.org/content/groups/public/")
  maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
  maven("https://repo.codemc.org/repository/maven-public/")
  maven("https://jitpack.io")
  mavenCentral()
}

dependencies {
  paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
  compileOnly("net.skinsrestorer", "skinsrestorer-api", "15.0.15")
  compileOnly("com.github.LeonMangler", "SuperVanish", "6.2.19")
  compileOnly("net.luckperms", "api", "5.4")
  compileOnly("net.megavex", "scoreboard-library-api", "2.1.10")
}

tasks {
  compileJava {
    options.release = 21
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
}

bukkitPluginYaml {
  main = "icu.suc.megawalls78.MegaWalls78"
  authors = listOf("557")
  apiVersion = "1.21"
  load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
  depend = listOf("SkinsRestorer", "SuperVanish", "LuckPerms")
  libraries = listOf("net.megavex:scoreboard-library-api:2.1.10", "net.megavex:scoreboard-library-implementation:2.1.10", "net.megavex:scoreboard-library-modern:2.1.10")
  prefix = "MW78"
  defaultPermission = Permission.Default.OP
  permissions {
    register("mw78.*") {
      children("mw78.id", "mw78.shout")
    }
    register("mw78.id") {
      default = Permission.Default.TRUE
    }
    register("mw78.map") {
      default = Permission.Default.TRUE
    }
    register("mw78.shout") {
      default = Permission.Default.TRUE
    }
    register("mw78.suicide") {
      default = Permission.Default.TRUE
    }
    register("mw78.energy") {
      default = Permission.Default.OP
    }
  }
  commands {
    register("id") {
      permission = "mw78.id"
      usage = "/id <identity>"
    }
    register("map") {
      permission = "mw78.map"
      usage = "/map"
    }
    register("shout") {
      permission = "mw78.shout"
      usage = "/shout <message>"
    }
    register("suicide") {
      permission = "mw78.suicide"
      aliases = listOf("kill", "die")
      usage = "/suicide"
    }
    register("energy") {
      permission = "mw78.energy"
      usage = "/energy"
    }
  }
}
