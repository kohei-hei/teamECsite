package com.internousdev.anemone.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.internousdev.anemone.dto.ProductInfoDTO;
import com.internousdev.anemone.util.DBConnector;

public class ProductInfoDAO {


	/*	商品情報取得(1つの詳細) 単体検索 メソッド---------------------------------------------------
		productIdを渡すと、その商品情報が全てセットされたdtoが返されます。
	-------------------------------------------------------------------------------------------------*/
	public ProductInfoDTO getProductInfo(int productId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		ProductInfoDTO dto = new ProductInfoDTO();

		try {
			String sql = "select * from product_info where product_id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, productId);
			ResultSet rs = ps.executeQuery();

			//商品情報を全てdtoにセット
			if(rs.next()) {
				dto.setProductIdNumber(rs.getInt("product_id"));
				dto.setProductName(rs.getString("product_name"));
				dto.setProductNameKana(rs.getString("product_name_kana"));
				dto.setProductDescription(rs.getString("product_description"));
				dto.setProductCategoryId(rs.getInt("category_id"));
				dto.setProductPrice(rs.getInt("price"));
				dto.setProductImageFilePath(rs.getString("image_file_path"));
				dto.setProductImageFileName(rs.getString("image_file_name"));
				dto.setProductReleaseDate(rs.getDate("release_date"));
				dto.setProductReleaseCompany(rs.getString("release_company"));
				dto.setProductStatus(rs.getInt("status"));
				dto.setProductRegistDate(rs.getDate("regist_date"));
				dto.setProductUpdateDate(rs.getDate("update_date"));
			}
		} catch( SQLException e) {
			e.printStackTrace();
		} finally {
			try{
				con.close();
			} catch ( SQLException e) {
				e.printStackTrace();
			}
		}
		return dto;
	}


	/*	メソッドの使い方-------------------------------------------------------------------------------------
 		このメソッドはキーワード、カテゴリー検索します。
		キーワードの配列を渡してください。そのキーワードでテーブル内を検索します。
		カテゴリーの指定があれば指定して検索します。

		商品一覧を表示したい場合はkeywordsを空に、CategoryIdを0に指定してください。
	 ---------------------------------------------------------------------------------------------------*/
	public ArrayList<ProductInfoDTO> getProductInfoList(String[] keywords, int CategoryId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		ArrayList<ProductInfoDTO> dtoList = new ArrayList<ProductInfoDTO>();
		int i = 0;
		String sql = "select * from product_info";

		//カテゴリーが０以外ならそのカテゴリーを検索、０ならオールカテゴリーで検索します。
		if (CategoryId != 0) {
			sql += " where category_id=" + CategoryId ;
			i++;
		}
		//keywordsリストの中身を順番にセットしていきます
		//keywordsの中身（特に最初）に空文字がない場合のみsql文を追加していきます
		for(String keyword : keywords) {
			if(!(keyword.equals(""))) {
				//whereが入っているかどうかで分岐
				//app% で検索するとapple applestore など%以降が一致していなくても検索結果として出力されます。%pple も同様です appleを検索可能  ここでは%keyword%のようにしています。
				if(i==0) {
					sql +=  " where (product_name like '%" + keyword + "%' or product_name_kana like '%" + keyword + "%'";
					i += 2;
				} else if (i==1) {
					sql +=  " and ( (product_name like '%" + keyword + "%' or product_name_kana like '%" + keyword + "%')";
					i++;
				} else {
					sql +=  " or (product_name like '%" + keyword + "%' or product_name_kana like '%" + keyword + "%')";
					i++;
				}
			}
		}
		//カテゴリー検索とキーワードが入力されていたらカッコを閉じます。
		if(i >= 2) {
			sql += " )";
		}
		sql += " order by product_id ASC";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			//商品情報を全てdtoにセット
			while(rs.next()) {
				ProductInfoDTO dto = new ProductInfoDTO();
				dto.setProductIdNumber(rs.getInt("product_id"));
				dto.setProductName(rs.getString("product_name"));
				dto.setProductNameKana(rs.getString("product_name_kana"));
				dto.setProductDescription(rs.getString("product_description"));
				dto.setProductCategoryId(rs.getInt("category_id"));
				dto.setProductPrice(rs.getInt("price"));
				dto.setProductImageFilePath(rs.getString("image_file_path"));
				dto.setProductImageFileName(rs.getString("image_file_name"));
				dto.setProductReleaseDate(rs.getDate("release_date"));
				dto.setProductReleaseCompany(rs.getString("release_company"));
				dto.setProductStatus(rs.getInt("status"));
				dto.setProductRegistDate(rs.getDate("regist_date"));
				dto.setProductUpdateDate(rs.getDate("update_date"));
				dtoList.add(dto);
			}

		} catch( SQLException e) {
			e.printStackTrace();
		} finally {
			try{
				con.close();
			} catch ( SQLException e) {
				e.printStackTrace();
			}
		}
		return dtoList;
	}


	/*	おすすめ商品取得メソッド--------------------------------------------------------------------------------------------
		おすすめ商品の取得 (個数制限は3つにしてあります) 引数は(カテゴリーID、詳細情報が出ている商品ID）でお願いします。
		商品IDが必要な理由は、その商品と同じ商品をおすすめに出してしまわないように避けるためです。
	-----------------------------------------------------------------------------------------------------------------------*/
	public ArrayList<ProductInfoDTO> getRecomendItems(int categoryId, int productId) {
		String sql ="select * from product_info where category_id=? and product_id not in(?) order by rand() limit 0,3";
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		ArrayList<ProductInfoDTO> dtoList = new ArrayList<ProductInfoDTO>();

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, categoryId);
			ps.setInt(2, productId);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				ProductInfoDTO dto = new ProductInfoDTO();
				dto.setProductIdNumber(rs.getInt("product_id"));
				dto.setProductName(rs.getString("product_name"));
				dto.setProductNameKana(rs.getString("product_name_kana"));
				dto.setProductDescription(rs.getString("product_description"));
				dto.setProductCategoryId(rs.getInt("category_id"));
				dto.setProductPrice(rs.getInt("price"));
				dto.setProductImageFilePath(rs.getString("image_file_path"));
				dto.setProductImageFileName(rs.getString("image_file_name"));
				dto.setProductReleaseDate(rs.getDate("release_date"));
				dto.setProductReleaseCompany(rs.getString("release_company"));
				dto.setProductStatus(rs.getInt("status"));
				dto.setProductRegistDate(rs.getDate("regist_date"));
				dto.setProductUpdateDate(rs.getDate("update_date"));
				dtoList.add(dto);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}

		return dtoList;
	}

}
