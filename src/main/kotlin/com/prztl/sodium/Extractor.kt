/**
 * The MIT License
 * Copyright © 2017 Warren S
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.prztl.sodium

import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths

internal object Extractor {
	class LibVersionImpl(
			val name: String,
			override val version: String,
			val _arch: SystemInfo.CPUArch,
			val _os: SystemInfo.OS,
			override val tags: Map<String, String> = mapOf()
	) : LibVersion {
		val nameWithoutExtension: String
			get() = Paths.get(name).toFile().nameWithoutExtension
		val libSuffix: String
			get() = "." + Paths.get(name).toFile().extension

		override val arch: String
			get() = _arch.toString().toLowerCase()
		override val os: String
			get() = _os.toString().toLowerCase()
	}

	private val availableConfigurations = listOf<LibVersionImpl>(
			//version 1.0.12
			LibVersionImpl(
					"/libsodium-1.0.12.x64.osx.dylib",
					version = "1.0.12",
					_arch = SystemInfo.CPUArch.X64,
					_os = SystemInfo.OS.MAC
			),
			LibVersionImpl(
					"/libsodium-1.0.12.x64.linux.so",
					version = "1.0.12",
					_arch = SystemInfo.CPUArch.X64,
					_os = SystemInfo.OS.LINUX
			),

			//version 1.0.18
			LibVersionImpl(
					"/libsodium-1.0.18-osx-x64",
					version = "1.0.18",
					_arch = SystemInfo.CPUArch.X64,
					_os = SystemInfo.OS.MAC
			),
			LibVersionImpl(
					"/libsodium-1.0.18-linux-x64.so",
					version = "1.0.18",
					_arch = SystemInfo.CPUArch.X64,
					_os = SystemInfo.OS.LINUX
			),
			LibVersionImpl(
					"/libsodium-1.0.18-mingw-x32.dll",
					version = "1.0.18",
					_arch = SystemInfo.CPUArch.X32,
					_os = SystemInfo.OS.WINDOWS,
					tags = mapOf("runtime" to "mingw")
			),
			LibVersionImpl(
					"/libsodium-1.0.18-mingw-x64.dll",
					version = "1.0.18",
					_arch = SystemInfo.CPUArch.X64,
					_os = SystemInfo.OS.WINDOWS,
					tags = mapOf("runtime" to "mingw")
			),
			LibVersionImpl(
					"/libsodium-1.0.18-msvc142-x32.dll",
					version = "1.0.18",
					_arch = SystemInfo.CPUArch.X32,
					_os = SystemInfo.OS.WINDOWS,
					tags = mapOf("runtime" to "msvc", "runtime-version" to "142")
			),
			LibVersionImpl(
					"/libsodium-1.0.18-msvc142-x64.dll",
					version = "1.0.18",
					_arch = SystemInfo.CPUArch.X64,
					_os = SystemInfo.OS.WINDOWS,
					tags = mapOf("runtime" to "msvc", "runtime-version" to "142")
			)
	)

	const val LATEST_VERSION = "1.0.18"
	val AVAILABLE_VERSIONS = availableConfigurations.map { it.version }.toSet()

	private fun findMatchingVersion(version: String, tags: Map<String, String>): LibVersionImpl {
		val os = SystemInfo.detectOS() ?: throw RuntimeException("Unable to detect OS")
		val arch = SystemInfo.detectCPUArch() ?: throw RuntimeException("Unable to detect CPU architecture")

		val potentialVersions = availableConfigurations.filter { cfg ->
			cfg.version == version && cfg._os == os && cfg._arch == arch
		}.filter { cfg ->
			tags.all { (k, v) -> cfg.tags[k] == v }
		}

		if(potentialVersions.isEmpty()) {
			throw RuntimeException("Could not find a matching version")
		}

		return potentialVersions[0]
	}

	fun extract(version: String = LATEST_VERSION, tags: Map<String, String> = mapOf()): Pair<File, LibVersion> {
		val libVer = findMatchingVersion(version, tags)
		val file = File.createTempFile(libVer.nameWithoutExtension, libVer.libSuffix)
		val i = Extractor.javaClass.getResourceAsStream(libVer.name)
		val o = FileOutputStream(file)

		val buf = ByteArray(4096)
		while(true) {
			val bytesRead = i.read(buf)
			if( bytesRead == -1 ) {
				break
			}

			o.write(buf, 0, bytesRead)
		}
		o.close()

		return file to libVer
	}
}