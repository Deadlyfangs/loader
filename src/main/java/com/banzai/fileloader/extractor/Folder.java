package com.banzai.fileloader.extractor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Folder {

    private FolderType folderType;
    private String path;

    public Folder(FolderType folderType, String path) {
        this.folderType = folderType;
        this.path = path;
    }

}
