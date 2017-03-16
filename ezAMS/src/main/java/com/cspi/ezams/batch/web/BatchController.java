package com.cspi.ezams.batch.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.json.simple.JSONObject;

import java.io.PrintWriter;

import com.cspi.ezams.batch.service.BatchService;

@SuppressWarnings("unchecked")
@Controller
@Component("ezamsBatchController")
public class BatchController{

   protected Log log = LogFactory.getLog(this.getClass());

   @Autowired
   private BatchService batchService;
 
   @RequestMapping(value="/common/startBatch.do", method=RequestMethod.POST)
   public void readyBatch(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception{

   JSONObject obj = new JSONObject();
   boolean rtn;

  rtn = runBatch();

  PrintWriter writer;
  if(rtn){
   obj.put("message","배치 완료되었습니다.");
   }else{
   obj.put("message","배치 실패하였습니다.");
   }

   writer = response.getWriter();
   writer.print(obj);
   writer.close();
}
   public boolean runBatch() throws Exception{
      boolean rtn = batchService.startBatch();

      return rtn;
}
   public void onScheduler(){
    log.debug("배치 실행");

    try{
       runBatch();
     }catch(Exception e){
       log.debug(e);
     }
    }
}