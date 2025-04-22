package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;

    @Autowired
    public BookMstService(BookMstRepository bookMstRepository) {
        this.bookMstRepository = bookMstRepository;
    }

    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }

    public boolean validation (BookMstDto bookMstDto, Model model){
        // ユーザーが入力した書籍ISBNと書籍名を取得
        String inputBookId = bookMstDto.getIsbn();
        String inputBookName = bookMstDto.getTitle();

        boolean errBookNameFlg = false;
        boolean errBookIsbnFlg = false;
        boolean errBookNameLengthFlg = false;
        boolean errBookIsbnLengthFlg = false;
        boolean errBookIsbnPatternFlg = false;


        if (inputBookId == null || inputBookId.trim().isEmpty()) {
            errBookIsbnFlg = true;  // 入力なしエラー
            model.addAttribute("errorIsbn","ISBNは必須です。");
        } else {
            String trimmed = inputBookId.trim();
        
            if (trimmed.length() != 13) {
                errBookIsbnLengthFlg = true;  // 13桁じゃない
                model.addAttribute("errorIsbn","ISBNは13桁で入力してください。");
            }
        
            if (!trimmed.matches("^[0-9]+$")) {
                errBookIsbnPatternFlg = true;  // 半角数字じゃない
                model.addAttribute("errorIsbn","ISBNは半角数字で入力してください。");
            }
        }
        
        if (inputBookName == null || inputBookName.trim().isEmpty()){
            errBookNameFlg = true;//入力なしエラー
            model.addAttribute("errorTitle","書籍名は必須です。");
        }else if(inputBookName.length() > 255){
            errBookNameLengthFlg = true;//255文字以内じゃない
            model.addAttribute("errorTitle","書籍名は255文字以内で入力してください。");
        }


        // エラーフラグのチェック
        if (errBookIsbnFlg || errBookNameFlg || errBookNameLengthFlg || errBookIsbnLengthFlg || errBookIsbnPatternFlg) {
            return true;
        }
        
        
        
        return false;

    }

    public int selectByIsbn(BookMstDto bookMstDto){
        int isbnlExist = this.bookMstRepository.selectByIsbn(bookMstDto.getIsbn());
        return isbnlExist;
    }
    

    public void save(BookMst bk) {
        this.bookMstRepository.save(bk);

    }

}
