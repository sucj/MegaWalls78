import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.Permission
import xyz.jpenilla.resourcefactory.paper.paperPluginYaml

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
  maven("https://repo.codemc.org/repository/maven-public/")
  maven("https://jitpack.io")
  maven("https://repo.dmulloy2.net/repository/public/")
  mavenCentral()
}

dependencies {
  paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
  compileOnly("net.skinsrestorer", "skinsrestorer-api", "15.0.15")
  compileOnly("com.github.LeonMangler", "SuperVanish", "6.2.19")
  compileOnly("net.luckperms", "api", "5.4")
  compileOnly("net.megavex", "scoreboard-library-api", "2.1.10")
  compileOnly("com.comphenix.protocol", "ProtocolLib", "5.3.0-SNAPSHOT")
  compileOnly("redis.clients", "jedis", "5.1.3")
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
  depend = listOf("SkinsRestorer", "SuperVanish", "LuckPerms", "ProtocolLib")
  libraries = listOf("redis.clients:jedis:5.1.3", "net.megavex:scoreboard-library-api:2.1.10", "net.megavex:scoreboard-library-implementation:2.1.10", "net.megavex:scoreboard-library-modern:2.1.10")
  prefix = "MW78"
  permissions {
    register("mw78.id") {
      default = Permission.Default.TRUE
    }
    register("mw78.map") {
      default = Permission.Default.TRUE
    }
    register("mw78.shout") {
      default = Permission.Default.TRUE
    }
    register("mw78.surface") {
      default = Permission.Default.TRUE
    }
    register("mw78.suicide") {
      default = Permission.Default.TRUE
    }
    register("mw78.energy") {
      default = Permission.Default.FALSE
    }
    register("mw78.start") {
      default = Permission.Default.FALSE
    }
    register("mw78.cancel") {
      default = Permission.Default.FALSE
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
    register("surface") {
      permission = "mw78.surface"
      aliases = listOf("spawn")
      usage = "/surface"
    }
    register("suicide") {
      permission = "mw78.suicide"
      aliases = listOf("kill", "die")
      usage = "/suicide"
    }
    register("energy") {
      permission = "mw78.energy"
      usage = "/energy [energy]"
    }
    register("start") {
      permission = "mw78.start"
      usage = "/start"
    }
    register("cancel") {
      permission = "mw78.cancel"
      usage = "/cancel"
    }
  }
}
