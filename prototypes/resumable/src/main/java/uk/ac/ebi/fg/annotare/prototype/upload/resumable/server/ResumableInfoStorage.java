package uk.ac.ebi.fg.annotare.prototype.upload.resumable.server;

import java.util.HashMap;

public class ResumableInfoStorage {

    //Single instance
    private ResumableInfoStorage() {
    }

    private static ResumableInfoStorage sInstance;

    public static synchronized ResumableInfoStorage getInstance() {
        if (sInstance == null) {
            sInstance = new ResumableInfoStorage();
        }
        return sInstance;
    }

    private HashMap<String, ResumableInfo> mMap = new HashMap<>();

    /**
     * Get ResumableInfo from mMap or Create a new one.
     * @param resumableChunkSize
     * @param resumableTotalSize
     * @param resumableIdentifier
     * @param resumableFilename
     * @param resumableRelativePath
     * @param resumableFilePath
     * @return
     */
    public synchronized ResumableInfo get(int resumableChunkSize, long resumableTotalSize,
                                          String resumableIdentifier, String resumableFilename,
                                          String resumableRelativePath, String resumableFilePath) {

        ResumableInfo info = mMap.get(resumableIdentifier);

        if (info == null) {
            info = new ResumableInfo();

            info.resumableChunkSize     = resumableChunkSize;
            info.resumableTotalSize     = resumableTotalSize;
            info.resumableIdentifier    = resumableIdentifier;
            info.resumableFilename      = resumableFilename;
            info.resumableRelativePath  = resumableRelativePath;
            info.resumableFilePath      = resumableFilePath;

            mMap.put(resumableIdentifier, info);
        }
        return info;
    }

    public void remove(ResumableInfo info) {
        mMap.remove(info.resumableIdentifier);
    }
}