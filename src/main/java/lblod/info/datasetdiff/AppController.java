package lblod.info.datasetdiff;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import mu.semte.ch.lib.utils.ModelUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

@RestController
@Slf4j
public class AppController {

  public AppController() {
  }

  @PostMapping(value = "/diff", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ByteArrayResource> delta(@RequestPart("a") MultipartFile modelA,
      @RequestPart("b") MultipartFile modelB)
      throws Exception {
    var a = ModelUtils.toModel(modelA.getInputStream(), ModelUtils.filenameToLang(modelA.getOriginalFilename()));
    var b = ModelUtils.toModel(modelB.getInputStream(), ModelUtils.filenameToLang(modelB.getOriginalFilename()));

    var intersect = ModelUtils.intersection(a, b);
    var diffA = ModelUtils.difference(a, b);
    var diffB = ModelUtils.difference(b, a);

    var timestamp = System.currentTimeMillis();
    File zipFile = new File(FileUtils.getTempDirectory(), timestamp + ".zip");

    var fileIntersect = new File(FileUtils.getTempDirectory(), "intersect.ttl");
    var fileDiffAB = new File(FileUtils.getTempDirectory(), "diff-ab.ttl");
    var fileDiffBA = new File(FileUtils.getTempDirectory(), "diff-ba.ttl");

    FileUtils.writeByteArrayToFile(fileIntersect, ModelUtils.toBytes(intersect, Lang.TURTLE));

    FileUtils.writeByteArrayToFile(fileDiffAB, ModelUtils.toBytes(diffA, Lang.TURTLE));
    FileUtils.writeByteArrayToFile(fileDiffBA, ModelUtils.toBytes(diffB, Lang.TURTLE));

    ZipParameters zipParameters = new ZipParameters();
    zipParameters.setIncludeRootFolder(false);
    try (var zip = new ZipFile(zipFile)) {
      zip.addFile(fileIntersect);
      zip.addFile(fileDiffAB);
      zip.addFile(fileDiffBA);
    }

    var zip = FileUtils.readFileToByteArray(zipFile);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFile.getName() + "\"")
        .body(new ByteArrayResource(zip));
  }
}
