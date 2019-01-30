package com.internousdev.anemone.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.anemone.dao.ProductInfoDAO;
import com.internousdev.anemone.dto.ProductInfoDTO;
import com.internousdev.anemone.util.InputChecker;
import com.opensymphony.xwork2.ActionSupport;

public class SearchItemAction extends ActionSupport implements SessionAware {

	private int categoryId;
	private String keywords;
	private List<String> keywordsErrorMessageList = new ArrayList<String>();
	private List<ProductInfoDTO> productInfoDtoList = new ArrayList<ProductInfoDTO>();
	private Map<String,Object> session;

	public String execute(){

		//キーワード検索用の仮変数
		String tempKeywords = null;

		//セッションタイムアウト時の処理
		if(!session.containsKey("mCategoryList")){
			return "sessionError";
		}

		InputChecker inputChecker = new InputChecker();


		if(StringUtils.isBlank(keywords)){
		//keywordsが空だったらtrue(なだけじゃなくて、検索欄に空白(半角/全角スペース)だけ入れられてしまっている時もtrueになる)

			tempKeywords = "";
			//tempKeywordsの中身を完全な空(=未入力の状態)に統一している
		}

		else{
		//空白以外の何かしらの文字が入力されているとき

			/*
			 * 検索窓に入力された値(keywords)が、空白を含めて検索前後で形が変わらないように
			 * tempKeywordsという変数を用意し、tempKeywords内で検索処理を行うための空白変換等の所作を行っている
			 * (⇒keywords内の文字列はそのまま保持されて戻る)
			 * */

			tempKeywords = keywords.replaceAll("　", " ").replaceAll("\\s{2,}", " ");
			//全角スペースを半角スペースに直し、更にその半角スペースが2つ以上続いている場合1つ分にギュッと圧縮している
			//例：酒　　　炭酸　　　　健康 ⇒ 酒   炭酸    健康 ⇒ 酒 炭酸 健康

		}

		if(!(tempKeywords.equals(""))){

			keywordsErrorMessageList = inputChecker.doCheck("検索ワード", keywords, 0, 16, true, true, true, true, false, true, false, true, true);
			//検索ワード欄入力値のチェック（falseなのは半角/全角記号のみ）文字数17字以上もエラーになる

			if(keywordsErrorMessageList.size() >0){
				return SUCCESS;
			}
			//↑検索ワードにエラーがあった場合、エラーメッセージが代入されているのでtrueになる

		}

		ProductInfoDAO productInfoDAO = new ProductInfoDAO();

			productInfoDtoList = productInfoDAO.getProductInfoList(tempKeywords.split(" "), categoryId);
			//引数について：splitメソッドで検索ワードを" "(半角スペース)があるごとに分割し、1つずつ配列の要素として格納
			//getProductInfoListメソッド内の拡張for文で各要素ごとに繰り返し検索処理（=OR検索）をしています


		Iterator<ProductInfoDTO> iterator = productInfoDtoList.iterator();
		if(!(iterator.hasNext())){
			productInfoDtoList = null;
		}


		session.put("productInfoDtoList", productInfoDtoList);

		return SUCCESS;
	}


	public int getCategoryId(){
		return categoryId;
	}

	public void setCategoryId(int categoryId){
		this.categoryId = categoryId;
	}

	public String getKeywords(){
		return keywords;
	}

	public void setKeywords(String keywords){
		this.keywords = keywords;
	}

	public List<String> getKeywordsErrorMessageList(){
		return keywordsErrorMessageList;
	}

	public void setKeywordsErrorMessageList(List<String> keywordsErrorMessageList){
		this.keywordsErrorMessageList = keywordsErrorMessageList;
	}

	public List<ProductInfoDTO> getProductInfoDtoList(){
		return productInfoDtoList;
	}

	public void setProductInfoDtoList(List<ProductInfoDTO> productInfoDtoList){
		this.productInfoDtoList = productInfoDtoList;
	}

	public Map<String, Object> getSession(){
		return session;
	}

	public void setSession(Map<String, Object> session){
		this.session = session;
	}

}
