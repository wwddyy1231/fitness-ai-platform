package com.fitnessai.platform.file.service;
import com.fitnessai.platform.common.exception.BusinessException;
import com.fitnessai.platform.file.vo.FileVO;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class FileStorageService {
 private static final Set<String> IMAGE_TYPES=Set.of("image/jpeg","image/png","image/webp","image/gif");
 private final Path root; private final String publicPrefix;
 public FileStorageService(@Value("${storage.local.root}") String root,@Value("${storage.local.public-prefix}") String prefix){this.root=Path.of(root).toAbsolutePath().normalize();this.publicPrefix=prefix;}
 public FileVO storeImage(MultipartFile file,String folder){
  if(file.isEmpty()||!IMAGE_TYPES.contains(file.getContentType()))throw new BusinessException(40010,"仅支持 JPG、PNG、WebP、GIF 图片");
  String original=Paths.get(file.getOriginalFilename()==null?"image":file.getOriginalFilename()).getFileName().toString();
  String ext=original.contains(".")?original.substring(original.lastIndexOf('.')).toLowerCase():"";
  String name=UUID.randomUUID()+ext; Path dir=root.resolve(folder).normalize(); Path target=dir.resolve(name).normalize();
  if(!target.startsWith(root))throw new BusinessException(40011,"非法文件路径");
  try{Files.createDirectories(dir);file.transferTo(target);}catch(IOException e){throw new BusinessException(50010,"文件保存失败");}
  return new FileVO(original,name,publicPrefix+"/"+folder+"/"+name,file.getSize(),file.getContentType());
 }
 public Path root(){return root;}
}
