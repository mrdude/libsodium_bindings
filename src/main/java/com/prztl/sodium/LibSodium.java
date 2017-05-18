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
package com.prztl.sodium;

import jnr.ffi.annotations.Out;
import jnr.ffi.byref.NativeLongByReference;

public interface LibSodium
{
	/**
	 * Initializes libsodium
	 * @return 0 on success, -1 on failure, 1 if the library is already initialized
	 */
	int sodium_init();
	
	/**
	 * Encrypts a message using the XChaCha20Poly1305 AEAD construction
	 * @param ciphertext the ciphertext
	 * @param ciphertext_len the length of the ciphertext
	 * @param message the plaintext message to encrypt
	 * @param message_len the length of the plaintext message
	 * @param additionaldata the additional data
	 * @param additionaldata_len the length of the additional data
	 * @param nsec should always be NULL -- isn't used by this construction
	 * @param public_nonce the public nonce -- should be NONCE bytes
	 * @param key the key -- should be KEY bytes
	 * @return
	 */
	int crypto_aead_xchacha20poly1305_ietf_encrypt(@Out byte[] ciphertext, NativeLongByReference ciphertext_len,
	                                               byte[] message, long message_len,
	                                               byte[] additionaldata, long additionaldata_len,
	                                               long nsec,
	                                               byte[] public_nonce,
	                                               byte[] key);
	
	/**
	 * Decrypts a message using the XChaCha20Poly1305 AEAD construction
	 * @param plaintext the plaintext
	 * @param plaintext_len the length of the plaintext
	 * @param nsec should always be NULL -- isn't used by this construction
	 * @param ciphertext the ciphertext
	 * @param ciphertext_len the length of the ciphertext
	 * @param additionaldata the additional data
	 * @param additionaldata_len the length of the additional data
	 * @param public_nonce the public nonce -- should be NONCE bytes
	 * @param key the key -- should be KEY bytes
	 * @return 0 if the message was successfully decrypted, != 0 if decryption failed
	 */
	int crypto_aead_xchacha20poly1305_ietf_decrypt(@Out byte[] plaintext, NativeLongByReference plaintext_len,
	                                               long nsec,
	                                               byte[] ciphertext, long ciphertext_len,
	                                               byte[] additionaldata, long additionaldata_len,
	                                               byte[] public_nonce,
	                                               byte[] key);
}
