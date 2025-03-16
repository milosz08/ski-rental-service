package pl.polsl.skirentalservice.domain;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.s3.FetchedObjectData;
import pl.polsl.skirentalservice.core.s3.S3Bucket;
import pl.polsl.skirentalservice.core.s3.S3ClientBean;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/resources/*")
public class GetStaticResourceServlet extends HttpServlet {
    private final S3ClientBean s3ClientBean;

    @Inject
    public GetStaticResourceServlet(S3ClientBean s3ClientBean) {
        this.s3ClientBean = s3ClientBean;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final HttpSession httpSession = req.getSession();
        if (httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getAttributeName()) == null) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        final String resourcePath = StringUtils.substringAfter(req.getRequestURI(), "/resources/");
        final String[] content = resourcePath.split("/");

        final S3Bucket bucket = S3Bucket.getBucketName(content[0]);
        final String name = content[1];
        if (bucket == null || StringUtils.isBlank(name)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final FetchedObjectData objectData = s3ClientBean.getObject(bucket, name);
        if (objectData == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        res.setContentType(objectData.contentType());
        res.setContentLength((int) objectData.contentLength());

        final OutputStream out = res.getOutputStream();
        out.write(objectData.data());
        out.close();
    }
}
