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

object Sodium {
	private val libsodium: com.prztl.sodium.LibSodium
	init {
		val file = Extractor.extract()
		com.prztl.sodium.Sodium.libsodium = jnr.ffi.LibraryLoader.create(LibSodium::class.java).load(file.absolutePath)
		if(com.prztl.sodium.Sodium.libsodium.sodium_init() == -1) {
			println("Failed to initialize libsodium")
			throw RuntimeException("Failed to initialize libsodium")
		}
	}

	object Crypto {
		object Aead {
			object XChaCha20Poly1305 {
				val NONCE_BYTES = 24 //crypto_aead_xchacha20poly1305_ietf_NPUBBYTES
				val KEY_BYTES = 32 //crypto_aead_xchacha20poly1305_ietf_KEYBYTES
				val A_BYTES = 16 //crypto_aead_xchacha20poly1305_ietf_ABYTES

				/**
				 * Encrypts the given data and returns the ciphertext
				 */
				fun encrypt(message: ByteArray, additionalData: ByteArray, nonce: ByteArray, key: ByteArray): ByteArray {
					assert(nonce.size == com.prztl.sodium.Sodium.Crypto.Aead.XChaCha20Poly1305.NONCE_BYTES)
					assert(key.size == com.prztl.sodium.Sodium.Crypto.Aead.XChaCha20Poly1305.KEY_BYTES)

					val ciphertext = ByteArray(message.size + com.prztl.sodium.Sodium.Crypto.Aead.XChaCha20Poly1305.A_BYTES)
					val ciphertext_len = jnr.ffi.byref.NativeLongByReference(0L)

					val res = com.prztl.sodium.Sodium.libsodium.crypto_aead_xchacha20poly1305_ietf_encrypt(ciphertext, ciphertext_len,
							message, message.size.toLong(),
							additionalData, additionalData.size.toLong(),
							0L,
							nonce,
							key)

					if( res != 0 ) {
						throw RuntimeException("Could not encrypt packet")
					}

					return java.util.Arrays.copyOf(ciphertext, ciphertext_len.toInt())
				}

				/**
				 * Decrypts the given data and authenticates the additional data. Returns the plaintext, or NULL if
				 * the ciphertext couldn't be decrypted.
				 */
				fun decrypt(ciphertext: ByteArray, additionalData: ByteArray, nonce: ByteArray, key: ByteArray): ByteArray? {
					assert(nonce.size == com.prztl.sodium.Sodium.Crypto.Aead.XChaCha20Poly1305.NONCE_BYTES)
					assert(key.size == com.prztl.sodium.Sodium.Crypto.Aead.XChaCha20Poly1305.KEY_BYTES)

					val plaintext = ByteArray(ciphertext.size - com.prztl.sodium.Sodium.Crypto.Aead.XChaCha20Poly1305.A_BYTES)
					val plaintext_len = jnr.ffi.byref.NativeLongByReference(0L)

					val res = com.prztl.sodium.Sodium.libsodium.crypto_aead_xchacha20poly1305_ietf_decrypt(plaintext, plaintext_len,
							0L,
							ciphertext, ciphertext.size.toLong(),
							additionalData, additionalData.size.toLong(),
							nonce,
							key)

					return if(res == 0) {
						java.util.Arrays.copyOf(plaintext, plaintext_len.value.toInt())
					} else {
						null //decryption failed!
					}
				}
			}
		}
	}
}