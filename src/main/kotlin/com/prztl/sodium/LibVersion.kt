package com.prztl.sodium

interface LibVersion {
	val version: String
	val arch: String
	val os: String
	val tags: Map<String, String>
}
