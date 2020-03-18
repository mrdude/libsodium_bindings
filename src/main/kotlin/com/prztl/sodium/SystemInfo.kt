package com.prztl.sodium

internal object SystemInfo {
	enum class OS {
		WINDOWS, LINUX, MAC
	}

	enum class CPUArch {
		X32,
		X64
		//ARM //TODO we don't have ARM binaries yet
	}

	fun detectOS(): OS? {
		val osName = System.getProperty("os.name")
		return when {
			osName.equals("Linux", ignoreCase = true) -> OS.LINUX
			osName.equals("Mac OS X", ignoreCase = true) -> OS.MAC
			osName.contains("Windows") -> OS.WINDOWS
			else -> null
		}
	}

	/**
	 * Attempts to detect CPU architecture from the value of the "os.arch" property.
	 *
	 * TODO test this on more systems
	 *
	 * So far, I've tested on:
	 *  - 64-bit macOS running Oracle's Java 1.8.0_121 release ("os.arch" = "x86_64")
	 */
	fun detectCPUArch(): CPUArch? {
		//TODO need to test this on more systems
		/*

		 */
		val arch = System.getProperty("os.arch")
		return when {
			arch == "x86" -> CPUArch.X32
			arch == "x86_64" || arch == "amd64" -> CPUArch.X64
			else -> null
		}
	}
}
