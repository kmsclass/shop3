package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dao.ItemDao;
import dao.UserDao;

@Service   //@Component + Service 기능. : Controller와 Dao의 중간 연결 역할
public class ShopService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private UserDao userDao;
	
	public List<Item> getItemList() {
		return itemDao.list();
	}
	public Item getItem(Integer id) {
		return itemDao.selectOne(id);
	}
	public void itemCreate(Item item, HttpServletRequest request) {
		//item.getPicture() : 업로드된 파일
		if(item.getPicture() != null && !item.getPicture().isEmpty()) {//업로드 파일 존재.
			uploadFileCreate(item.getPicture(),request,"img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		//item 테이블에 내용 저장
		itemDao.insert(item);
	}
	//MultipartFile의 데이터를 파일로 저장
	private void uploadFileCreate
	     (MultipartFile picture, HttpServletRequest request, String path) {
		String orgFile = picture.getOriginalFilename(); //업로드된 파일의 이름
		String uploadPath = request.getServletContext().getRealPath("/") +path;
		File fpath = new File(uploadPath);
		if(!fpath.exists()) fpath.mkdirs(); //업로드 폴더가 없는 경우 폴더 생성
		try {
			//picture : 업로드된 파일의 내용 저장
			// transferTo : 업로드된 파일의 내용을 File로 저장
			picture.transferTo(new File(uploadPath+orgFile));
		} catch(Exception e) {
			e.printStackTrace();
		}				
	}
	public void itemUpdate( Item item, HttpServletRequest request) {
		// TODO Auto-generated method stub
		if(item.getPicture()!=null && !item.getPicture().isEmpty()) {
			uploadFileCreate(item.getPicture(),request,"img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
			
		}
		itemDao.update(item);
	}
	public void itemDelete(Integer id) {
		itemDao.delete(id);
		
	}
	public void insert(User user) {
		userDao.insert(user);
	}
	public User selectUserOne(String userid) {		
		return userDao.selectUserOne(userid);
	}
	public List<Sale> salelist(String id) {
		return new ArrayList<Sale>();
	}
	public List<SaleItem> saleItemList(int saleid) {
		return new ArrayList<SaleItem>();
	}
}
    