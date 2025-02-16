package com.aloa.common.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.File;
import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
class DeletableFile {
    private File directory;
    private String fileName;
    private Consumer<File[]> delete;

    static DeletableFile of(String directory, String fileName, Consumer<File[]> delete) {
        return new DeletableFile(new File(directory), fileName, delete);
    }

    void delete(){
        var videoFileNames = directory.listFiles((dir, name) -> name.startsWith(fileName));

        delete.accept(videoFileNames);
    }
}
