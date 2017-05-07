# Kotlin bindings for libsodium

## What is this?
Kotlin bindings for [libsodium](https://github.com/jedisct1/libsodium) using jnr-ffi. More specifically,
these bindings allow access to the AEAD constructions that libsodium provides.

## Supported platforms:

* Mac OSX (x64)
* Linux (x64)

## Why not just use [Kalium](https://github.com/abstractj/kalium)?
Because, as of now, Kalium doesn't have bindings for libsodium's AEAD functions.


This was also a great way for me to learn about jnr-ffi.

## TODO

* Add bindings for the other libsodium functions
* Add precompiled binaries for other platforms