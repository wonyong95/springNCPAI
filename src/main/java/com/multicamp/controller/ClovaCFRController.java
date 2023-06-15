package com.multicamp.controller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
/*
ClientID
wvaof3v62r

ClientSecret
ywMnwBxqGfNu2hdE70KUEsZ23J7ZuOQkyduBCK6C
 * */
@RestController
public class ClovaCFRController {
	
	private String clientId="wvaof3v62r";
	private String clientSecret="ywMnwBxqGfNu2hdE70KUEsZ23J7ZuOQkyduBCK6C";
	
	private Logger log=LoggerFactory.getLogger(getClass());

	
	@GetMapping("/faceform")
	public ModelAndView faceform() {
		
		return new ModelAndView("clova/cfrform");
	}//---------------------
	
	@PostMapping("/cfrCelebrity")
	public Map<String, String> cfrResult(@RequestParam("image") MultipartFile mfile, HttpSession ses){
		Map<String, String> map=new HashMap<>();
		ServletContext ctx=ses.getServletContext();
		String upDir=ctx.getRealPath("/upload");
		
		log.info("upDir={}", upDir);
		
		File dir=new File(upDir);
		if(!dir.exists()) {
			dir.mkdirs();//upload디렉토리 생성
		}
		//첨부파일명 알아내기
		String fname=mfile.getOriginalFilename();
		File f=null;
		
		try {
			mfile.transferTo(f=new File(upDir, fname));//업로드 처리
			String fileName=f.getName();

			URL url=new URL(api_url);
			HttpURLConnection con=(HttpURLConnection)url.openConnection();
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			//파일내용을 구분하기 위한 구분자 문자열을 사용
			String boundary="---"+System.currentTimeMillis()+"---";
			//요청 헤더값 설정
			con.setRequestProperty("content-Type", "multipart/form-data; boundary="+boundary);
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
			con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

			OutputStream out=con.getOutputStream();//1byte기반 스트림
			PrintWriter pw=new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
			//2byte기반 스트림(문자기반으로 데이터전송)

			String line_feed="\r\n";//줄바꿈
			pw.append(""+boundary).append(line_feed);
			pw.append("Content-Disposition: form-data; name=\"image\"; filename=\""+fileName+"\"").append(line_feed);
			pw.append("Content-Type: "+ URLConnection.guessContentTypeFromName(fileName)).append(line_feed);
			pw.append(line_feed);
			pw.flush();

			FileInputStream fis=new FileInputStream(f);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", "error: "+e.getMessage());
		}

		
		return map;
	}//------------------------

}////////////////////////////////////////











