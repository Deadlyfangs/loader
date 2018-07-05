package com.banzai.fileloader.extractor.parameters;

import com.banzai.fileloader.extractor.Folder;
import com.banzai.fileloader.extractor.FolderType;

import java.io.File;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ConsumerParams {

    BlockingQueue<File> queue;
    Map<FolderType, Folder> folderMap;

}
