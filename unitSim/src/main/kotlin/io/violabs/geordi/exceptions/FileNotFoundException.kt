package io.violabs.geordi.exceptions

class FileNotFoundException(filename: String) : Exception("File not found: $filename")
