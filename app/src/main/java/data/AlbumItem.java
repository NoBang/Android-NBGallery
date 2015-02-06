package data;

        import java.util.ArrayList;

/**
 * 제품 이미지 클래스
 *
 * @author byeongnamno
 */
public class AlbumItem {

    /**
     * 아이디
     */
    public long id;

    /**
     * 카메라 모드인지 default false
     */
    public boolean isCam = false;

    /**
     * 썸네일 주소
     */
    public String thumbPath;

    /**
     * 폴더 이름
     */
    public String displayName;


    /**
     * 사진 리스트
     */
    public ArrayList<PhotoItem> arraylist;


    public AlbumItem() {

        arraylist = new ArrayList<>();
    }
}