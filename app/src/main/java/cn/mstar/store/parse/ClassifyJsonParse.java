package cn.mstar.store.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.mstar.store.entity.MoreClassifyData;
import cn.mstar.store.entity.MoreClassifySubData;
import cn.mstar.store.utils.L;

/**
 * 分类里面请求json数据解析
 * @author wenjundu 2015-7-9
 *
 */
public class ClassifyJsonParse {
	   public static void parseJson(JSONObject jsonObj,final ParseCallBack callback) { 
	        try { 
	        	if(jsonObj!=null){
	        		JSONArray moreClassifyArray = jsonObj.getJSONArray("data");
	        		//存放左侧目录List
	        		ArrayList<MoreClassifyData> moreClassifyList=new ArrayList<MoreClassifyData>();
	            	for(int i =0;i<moreClassifyArray.length();i++){
	            		MoreClassifyData moreClassifyData=new MoreClassifyData();
	            		moreClassifyData.setCategoryId(moreClassifyArray.getJSONObject(i).getString("categoryId"));
	            		moreClassifyData.setCategoryName(moreClassifyArray.getJSONObject(i).getString("categoryName"));
	            		moreClassifyData.setPic(moreClassifyArray.getJSONObject(i).getString("pic"));
	            		moreClassifyData.setParentId(moreClassifyArray.getJSONObject(i).getString("parentId"));
	         		
	            		
	            		JSONArray moreClassifySubArray=moreClassifyArray.getJSONObject(i).getJSONArray("subCategoryList");
						L.e("..." + moreClassifySubArray.length());
	            		//存放右侧产品List
	            		ArrayList<MoreClassifySubData>  moreClassifySubList=new ArrayList<MoreClassifySubData>();;
	            		for(int j=0;j<moreClassifySubArray.length();j++){

	            			MoreClassifySubData moreClassifySubData=new MoreClassifySubData();
	            			moreClassifySubData.setCategoryId(moreClassifySubArray.getJSONObject(j).getString("categoryId"));

	            			moreClassifySubData.setCategoryName(moreClassifySubArray.getJSONObject(j).getString("categoryName"));
	            			moreClassifySubData.setParentId(moreClassifySubArray.getJSONObject(j).getString("parentId"));
	            			moreClassifySubData.setPic(moreClassifySubArray.getJSONObject(j).getString("pic"));
	            			moreClassifySubData.setSubCategoryList(moreClassifySubArray.getJSONObject(j).optString("subCategoryList"));

	            			moreClassifySubList.add(moreClassifySubData);
	            		}
	            		moreClassifyData.setMoreclassifySubArr(moreClassifySubList);
	            		
	            		moreClassifyList.add(moreClassifyData);
	            	}
	            	 
	            	 
	            	if(callback!=null){
	            		callback.onSuccess(moreClassifyList);
	            	}else{
	            	
	            	}
	        	}else{
	        	}
	            
	            	
	        } catch (JSONException e) { 
	            e.printStackTrace(); 
	            if(callback!=null){
	            	callback.onFailure(e.toString());
	            }
	        } 
	    } 
	   
		public interface ParseCallBack{
			public void  onSuccess(ArrayList<MoreClassifyData> list);
			public void onFailure(String failresult);
		}
}
