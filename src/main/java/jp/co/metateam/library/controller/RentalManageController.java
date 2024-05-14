package jp.co.metateam.library.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.el.ELException;
import jakarta.validation.Valid;
import jp.co.metateam.library.service.AccountService;
import jp.co.metateam.library.service.RentalManageService;
import jp.co.metateam.library.service.StockService;
import lombok.extern.log4j.Log4j2;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.values.RentalStatus;
import jp.co.metateam.library.service.BookMstService;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.model.StockDto;



/**
 * 貸出管理関連クラスß
 */
@Log4j2
@Controller
public class RentalManageController {

    private final AccountService accountService;
    private final RentalManageService rentalManageService;
    private final StockService stockService;

    @Autowired
    public RentalManageController(
        AccountService accountService, 
        RentalManageService rentalManageService, 
        StockService stockService
    ) {
        this.accountService = accountService;
        this.rentalManageService = rentalManageService;
        this.stockService = stockService;
    }

    /**
     * 貸出一覧画面初期表示
     * @param model
     * @return
     */
    @GetMapping("/rental/index")
    public String index(Model model) {
        // 貸出管理テーブルから全件取得
        List <RentalManage> rentalManageList = this.rentalManageService.findAll();

        // 貸出一覧画面に渡すデータをmodelに追加
        model.addAttribute("rentalManageList", rentalManageList);

        // 貸出一覧画面に遷移
        return "rental/index";
    }

    @GetMapping("/rental/add")
    public String add(Model model) {
        List<Account> accounts = this.accountService.findAll();
        List<Stock> stockList = this.stockService.findAll();


        model.addAttribute("rentalStatus", RentalStatus.values());
        model.addAttribute("stockList", stockList);
        model.addAttribute("accounts", accounts);

        if (!model.containsAttribute("rentalManageDto")) {
            model.addAttribute("rentalManageDto", new RentalManageDto());
        }

        return "rental/add";
    }

    @PostMapping("/rental/add")
    public String register(@Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra, Model model) {
        try {
            if (result.hasErrors()) {
                throw new Exception("Validation error.");
            }
            // 登録処理
            this.rentalManageService.save(rentalManageDto);

            return "redirect:/rental/index";
        } catch (Exception e) {
            log.error(e.getMessage());

            ra.addFlashAttribute("rentalManageDto", rentalManageDto);
            ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);

            return "redirect:/rental/add";
        }
    }



@GetMapping("/rental/{id}/edit")
public String edit(@PathVariable("id") Long id, Model model) {
    List <Stock> stockList = this.stockService.findAll();  //在庫管理番号のプルダウンリスト作成
    List <Account> accounts = this.accountService.findAll(); //社員番号のプルダウンリスト作成
    List <RentalManage> rentalManageList = this.rentalManageService.findAll();
 
        model.addAttribute("stockList", stockList); //在庫管理番号のリストを表示（プルダウン）
        model.addAttribute("accounts", accounts);  //社員番号のリストを表示（プルダウン）
        model.addAttribute("rentalStatus", RentalStatus.values());  //貸出ステータスリスト（プルダウン）
 
 
        if (!model.containsAttribute("rentalManageDto")) {
            RentalManageDto rentalManageDto = new RentalManageDto();
            RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));
            rentalManageDto.setEmployeeId(rentalManage.getAccount().getEmployeeId());
            rentalManageDto.setExpectedReturnOn(rentalManage.getExpectedReturnOn());
            rentalManageDto.setExpectedRentalOn(rentalManage.getExpectedRentalOn());
            rentalManageDto.setStockId(rentalManage.getStock().getId());
            rentalManageDto.setStatus(rentalManage.getStatus());
            rentalManageDto.setId(rentalManage.getId());
 
            model.addAttribute("rentalManageDto", rentalManageDto);
        }
 
        return "rental/edit";
 
}

@PostMapping("/rental/{id}/edit")
public String update(@PathVariable("id") String id, @Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra, Model model) {
    try {
        RentalManage rentalManege = this.rentalManageService.findById(Long.valueOf(id));
        String errMsgOfStatus = rentalManageDto.validateStatus(rentalManege.getStatus());

        

        if (errMsgOfStatus!=null) {
            result.addError(new FieldError("rentalManageDto","status",errMsgOfStatus));
        }

        if(result.hasErrors()){
            throw new Exception("Validation error.");
        }
        // 更新処理
        this.rentalManageService.update(Long.valueOf(id),rentalManageDto);

        return "redirect:/rental/index";
    } catch (Exception e) {
        log.error(e.getMessage());

        ra.addFlashAttribute("rentalManageDto", rentalManageDto);
        ra.addFlashAttribute("org.springframework.validation.BindingResult.stockDto", result);

        List<Account> accounts = this.accountService.findAll();
        List<Stock> stockList = this.stockService.findAll();


        model.addAttribute("rentalStatus", RentalStatus.values());
        model.addAttribute("stockList", stockList);
        model.addAttribute("accounts", accounts);

        return "rental/edit";
    }
}
}

