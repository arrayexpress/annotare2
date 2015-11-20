package uk.ac.ebi.fg.annotare.prototype.upload.resumable.server;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;

@MultipartConfig
public class ResumableUploadServlet extends HttpServlet {

    public static final String UPLOAD_DIR = "/tmp/resumable_upload";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int resumableChunkNumber = getResumableChunkNumber(request);

        ResumableInfo info = getResumableInfo(request);

        RandomAccessFile raf = new RandomAccessFile(info.resumableFilePath, "rw");

        //Seek to position
        raf.seek((resumableChunkNumber - 1) * (long)info.resumableChunkSize);

        //Save to file
        InputStream is = isMultipart(request) ?
                request.getPart("file").getInputStream() : request.getInputStream();
        long read = 0;
        long content_length = request.getContentLength();
        byte[] bytes = new byte[1024 * 100];
        while(read < content_length) {
            int r = is.read(bytes);
            if (r < 0)  {
                break;
            }
            raf.write(bytes, 0, r);
            read += r;
        }
        raf.close();


        //Mark as uploaded.
        info.uploadedChunks.add(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber));
        if (info.checkIfUploadFinished()) { //Check if all chunks uploaded, and change filename
            ResumableInfoStorage.getInstance().remove(info);
            response.getWriter().print("All finished.");
        } else {
            response.getWriter().print("Uploaded.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int resumableChunkNumber = getResumableChunkNumber(request);

        ResumableInfo info = getResumableInfo(request);

        if (info.uploadedChunks.contains(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber))) {
            response.getWriter().print("Uploaded."); //This Chunk has been Uploaded.
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    private int getResumableChunkNumber(HttpServletRequest request) throws ServletException, IOException {
        return isMultipart(request) ?
                HttpUtils.toInt(partToString(request.getPart("resumableChunkNumber")), -1) :
                HttpUtils.toInt(request.getParameter("resumableChunkNumber"), -1);
    }

    private ResumableInfo getResumableInfo(HttpServletRequest request) throws ServletException, IOException {
        String base_dir = UPLOAD_DIR;

        int resumableChunkSize;
        long resumableTotalSize;
        String resumableIdentifier;
        String resumableFilename;
        String resumableRelativePath;

        if (isMultipart(request)) {
            resumableChunkSize = HttpUtils.toInt(partToString(request.getPart("resumableChunkSize")), -1);
            resumableTotalSize = HttpUtils.toLong(partToString(request.getPart("resumableTotalSize")), -1);
            resumableIdentifier = partToString(request.getPart("resumableIdentifier"));
            resumableFilename = partToString(request.getPart("resumableFilename"));
            resumableRelativePath = partToString(request.getPart("resumableRelativePath"));
        } else {
            resumableChunkSize = HttpUtils.toInt(request.getParameter("resumableChunkSize"), -1);
            resumableTotalSize = HttpUtils.toLong(request.getParameter("resumableTotalSize"), -1);
            resumableIdentifier = request.getParameter("resumableIdentifier");
            resumableFilename = request.getParameter("resumableFilename");
            resumableRelativePath = request.getParameter("resumableRelativePath");
        }

        //Here we add a ".temp" to every upload file to indicate NON-FINISHED
        new File(base_dir).mkdir();
        String resumableFilePath = new File(base_dir, resumableFilename).getAbsolutePath() + ".temp";

        ResumableInfoStorage storage = ResumableInfoStorage.getInstance();

        ResumableInfo info = storage.get(resumableChunkSize, resumableTotalSize,
                resumableIdentifier, resumableFilename, resumableRelativePath, resumableFilePath);
        if (!info.vaild()) {
            storage.remove(info);
            throw new ServletException("Invalid request params.");
        }
        return info;
    }

    private boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return null != contentType && contentType.startsWith("multipart");
    }

    private String partToString(Part part) throws IOException {

        BufferedReader br;
        StringBuilder sb = new StringBuilder();

        String line;
        try (InputStream is = part.getInputStream()) {
            if (null != is) {
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
        }

        return sb.toString();
    }
}