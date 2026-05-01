![Version](https://img.shields.io/badge/Minecraft-1.21.x-orange)
![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Spigot%20%7C%20Purpur-green)
![KingdomsX](https://img.shields.io/badge/Integration-KingdomsX-blue)
![MythicMobs](https://img.shields.io/badge/Integration-MythicMobs-red)

# 🌊 MobWave

**MobWave** is a powerful, high-performance siege plugin developed by **Nuova Aurora Studio** for **Paper, Spigot, and Purpur (1.21+)**. It brings a new level of challenge to **KingdomsX** servers by spawning dynamic waves of mobs (Vanilla or MythicMobs) that target kingdom regions, forcing players to defend their homes in epic PvE battles.

---

## 🌟 Key Features

* ⚔️ **Siege Mechanics:** Trigger automated or manual mob assaults on specific Kingdom regions.
* 👹 **MythicMobs Support:** Seamlessly integrate custom mobs with complex skills and behaviors.
* 📊 **Smart Persistence:** Utilizes a local **H2 Database** with **HikariCP** for fast and reliable data tracking.
* 🎨 **Dynamic UI:** Real-time **Boss Bar** tracking for wave progress, mob counts, and countdowns.
* ⚙️ **Modern Frameworks:** Built with **ACF** for commands and **Okaeri Configs** for a developer-friendly experience.
* 🚀 **Zero Lag:** Highly optimized spawning logic and asynchronous database operations to ensure stable TPS.

---

## 🛠 Commands & Permissions

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/mw help` | `mobwave.admin` | Shows the help menu. |
| `/mw start` | `mobwave.admin` | Starts a wave at your current location. |
| `/mw stop` | `mobwave.admin` | Stops the active wave nearby or all waves (console). |
| `/mw spawn <kingdom>` | `mobwave.admin` | Spawns a wave for a specific kingdom. |
| `/mw reload` | `mobwave.admin` | Reloads the plugin configuration. |

---

## 📋 Configuration Preview

Customize your sieges in the `config.yml`:

```yaml
# Global Settings
wave-schedule-seconds: 600
range-from-kingdom-center: 50
delay-before-first-wave: 30
max-concurrent-mobs: 20

# Limits
waves-per-day-limit: 3
min-cooldown-minutes: 120
spawn-chance: 50

# Wave Definitions
waves:
  wave1:
    total-mobs: 10
    delay-to-next-wave: 30
    mobs:
      ZOMBIE: 100
  wave2:
    total-mobs: 15
    mobs:
      ZOMBIE: 50
      SKELETON: 50
```

---

[CENTER]
[URL='https://discord.gg/dukKzj8xqQ'][IMG]https://img.shields.io/badge/Discord-Join%20Our%20Community-7289da?style=for-the-badge&logo=discord[/IMG][/URL]
[/CENTER]

## 🏗 Technical Specifications

* **Minecraft Version:** 1.21.x
* **Server Software:** Paper, Spigot, Purpur
* **Java Version:** 21
* **Database:** H2 (Local)
* **Required Dependencies:** KingdomsX
* **Optional Dependencies:** MythicMobs

Developed with ❤️ by **Nuova Aurora Studio**
