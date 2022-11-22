pluginManagement {
	repositories {
		maven { url = uri("https://repo.spring.io/milestone") }
		gradlePluginPortal()
	}
}
rootProject.name = "after-action-report"
include("discord-command-gateway")
include("report-generator")
include("pubg-client")