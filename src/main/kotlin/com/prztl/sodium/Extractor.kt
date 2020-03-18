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

internal object Extractor {
	val SODIUM_VERSION = "1.0.12"
	private val LIB_MAC_X64 = "/libsodium-$SODIUM_VERSION.x64.osx.dylib"
	private val LIB_NIX_X64 = "/libsodium-$SODIUM_VERSION.x64.linux.so"

	data class LibVersion(val name: String, val libSuffix: String)
	private val LINUX = LibVersion(LIB_NIX_X64, ".so")
	private val MAC = LibVersion(LIB_MAC_X64, ".dylib")

	private val libVersion: LibVersion
		get() {
			val osname = System.getProperty("os.name")
			return if(osname.contains("linux", ignoreCase = true)) {
				LINUX
			} else if(osname.contains("mac", ignoreCase = true)) {
				MAC
			} else {
				throw RuntimeException("Unknown architecture/os")
			}
		}

	fun extract(): File {
		val file = File.createTempFile("libsodium-$SODIUM_VERSION-", libVersion.libSuffix)
		val i = Extractor.javaClass.getResourceAsStream(libVersion.name)
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

		return file
	}
}