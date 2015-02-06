package data;

/**
 * 제품 이미지 클래스
 *
 * @author byeongnamno
 */
public class PhotoItem {

    /**
     * 원본 파일 주소
     */
    public String filePath;

    /**
     * 이미지 아이디
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

}