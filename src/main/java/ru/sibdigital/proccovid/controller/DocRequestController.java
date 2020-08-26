package ru.sibdigital.proccovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.config.CurrentUser;
import ru.sibdigital.proccovid.model.*;
import ru.sibdigital.proccovid.repository.*;
import ru.sibdigital.proccovid.repository.specification.DocRequestPrsSearchCriteria;
import ru.sibdigital.proccovid.service.EmailService;
import ru.sibdigital.proccovid.service.RequestService;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
public class DocRequestController {

    @Autowired
    private ClsUserRepo clsUserRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private DocRequestPrsRepo docRequestPrsRepo;

    @Autowired
    private DocRequestWithPersonRepo docRequestWithPersonRepo;

    @Autowired
    private RequestService requestService;

    @Autowired
    private DocPersonRepo docPersonRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DocAddressFactRepo docAddressFactRepo;

    @Autowired
    private ClsDepartmentRepo clsDepartmentRepo;

    @Autowired
    private ClsDistrictRepo clsDistrictRepo;

    @Autowired
    private RegHistoryRequestRepo historyRequestRepo;

    private static final Logger log = LoggerFactory.getLogger(DocRequestController.class);

    private void addHistory(DocRequest docRequest, ClsUser clsUser){
        try{
            RegHistoryRequest rhr = new  RegHistoryRequest(docRequest, clsUser);
            historyRequestRepo.save(rhr);
        }catch (Exception ex){
            log.error("history exception =  " + ex.getMessage());
        }
    }

    @GetMapping("/doc_requests")
    public DocRequest requests(@RequestParam String inn,@RequestParam String ogrn, Map<String, Object> model) {
        //if(inn!=null & !inn.isBlank()){
        if(inn!=null & !inn.isEmpty()){
            return requestService.getLasRequestInfoByInn(inn);
        }

        if( ogrn!=null & !ogrn.isEmpty()){
            return requestService.getLastRequestInfoByOgrn(ogrn);
        }

        return null;
    }

    @GetMapping("/list_request/{id_department}/{status}")
    public Map<String, Object> listRequest(@PathVariable("id_department") Long idDepartment,
                                            @PathVariable("status") Integer status,
                                            @RequestParam(value = "id_type_request", required = false) Integer idTypeRequest,
                                            @RequestParam(value = "id_district", required = false) Integer idDistrict,
                                            @RequestParam(value = "innOrName", required = false) String innOrName,
                                            @RequestParam(value = "start", required = false) Integer start,
                                            @RequestParam(value = "count", required = false) Integer count) {

        int page = start == null ? 0 : start / 25;
        int size = count == null ? 25 : count;

        DocRequestPrsSearchCriteria searchCriteria = new DocRequestPrsSearchCriteria();
        searchCriteria.setIdDepartment(idDepartment);
        searchCriteria.setStatusReview(status);
        searchCriteria.setIdTypeRequest(idTypeRequest);
        searchCriteria.setIdDistrict(idDistrict);
        searchCriteria.setInnOrName(innOrName);

        Page<DocRequestPrs> docRequestPrsPage = requestService.getRequestsByCriteria(searchCriteria, page, size);

        Map<String, Object> result = new HashMap<>();
        result.put("data", docRequestPrsPage.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", docRequestPrsPage.getTotalElements());
        return result;
    }

    //  @GetMapping("/list_requestByInnAndName/{id_department}/{status}/{innOrName}")
    @GetMapping("/all_request/{status}")
    public Optional<List<DocRequestWithPerson>> all_request(@PathVariable("status") Integer status) {
        Optional<List<DocRequestWithPerson>> docRequests = docRequestWithPersonRepo.getAllByStatusId(status);
        return docRequests;
    }

    @PutMapping("/doc_requests/{id}/update")
    public ResponseEntity<String> updateItem(@PathVariable("id") DocRequest docRequest,
                                                @RequestBody DocRequest obj,
                                                @RequestHeader("Authorization") String token, HttpSession session){

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        Long oldDepartmentId = docRequest.getDepartment().getId();
        if (oldDepartmentId != obj.getDepartment().getId()) {
            ClsDepartment clsDepartment = clsDepartmentRepo.getOne(obj.getDepartment().getId());
            docRequest.setDepartment(clsDepartment);
            docRequest.setOld_department_id(oldDepartmentId);
            docRequest.setReassignedUser(clsUser);
            addHistory(docRequest, clsUser);
            docRequestRepo.save(docRequest);

            return ResponseEntity.ok().body("updated");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not updated");
    }

    @PutMapping("/doc_requests/{id}/process")
    public ResponseEntity<String> processItem(@PathVariable("id") DocRequest docRequest,
                                              @RequestBody DocRequest obj,
                                              @RequestHeader("Authorization") String token, HttpSession session) {

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClsUser clsUser = currentUser.getClsUser();

        Boolean changeFlag = true;
        Timestamp timeReview = new Timestamp(System.currentTimeMillis());

        if(docRequest.getStatusReview() != obj.getStatusReview()){

            changeFlag = true;

            if (obj.getStatusReview() == ReviewStatuses.CONFIRMED.getValue()) {
                docRequest.setRejectComment(obj.getRejectComment());
//                boolean massAccept = true;
//                if (massAccept == true){
//                    try {
//                        final Optional<List<DocRequest>> lastRequestByInn =
//                                docRequestRepo.getLastRequestByInnAndStatus(docRequest.getOrganization().getInn(), 0);
//                        if (lastRequestByInn.isPresent()) {
//                            final List<DocRequest> docRequests = lastRequestByInn.get()
//                                    .stream().filter(elem -> elem.getId() != docRequest.getId()).collect(Collectors.toList());
//                            for (DocRequest dr : docRequests) {
//                                dr.setStatusReview(obj.getStatusReview());
//                                dr.setTimeReview(timeReview);
//                                dr.setProcessedUser(clsUser);
//                            }
//                            docRequestRepo.saveAll(docRequests);
//                        }
//                    }catch (Exception ex){
//                        log.error("mass exception =  " + ex.getMessage());
//                    }
//                }
            }
            else if (obj.getStatusReview() == ReviewStatuses.REJECTED.getValue()) {
                if (obj.getRejectComment() == null || obj.getRejectComment().isBlank()){
                    return ResponseEntity.status(HttpStatus.OK).body("NO_REJECTED_COMMENT");
                }
                docRequest.setRejectComment(obj.getRejectComment());
            }
        }

        if(changeFlag){
            Long oldTypeRequestId = docRequest.getTypeRequest().getId();
            if (oldTypeRequestId != obj.getTypeRequest().getId()) {
                ClsTypeRequest clsTypeRequest = requestService.getClsTypeRequest(obj.getTypeRequest().getId());
                docRequest.setTypeRequest(clsTypeRequest);
            }
            docRequest.setStatusReview(obj.getStatusReview());
            docRequest.setTimeReview(timeReview);
            docRequest.setProcessedUser(clsUser);
            addHistory(docRequest, clsUser);
            docRequestRepo.save(docRequest);

            emailService.sendMessage(docRequest);
            return ResponseEntity.ok().body("updated");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not updated");
        }
    }

    @GetMapping("/doc_persons/{id_request}")
    public Optional<List<DocPerson>> getListPerson(@PathVariable("id_request") Long id_request){
        return docPersonRepo.findByDocRequest(id_request);
    }

    @GetMapping("/doc_address_fact/{id_request}")
    public Optional<List<DocAddressFact>> getListAddress(@PathVariable("id_request") Long id_request){
        return docAddressFactRepo.findByDocRequest(id_request);
    }

    @GetMapping("/cls_departments")
    public List<ClsDepartment> getListDepartments() {
        return clsDepartmentRepo.findAll(Sort.by("id"));
    }

    @GetMapping("/doc_requests/{id_request}")
    public Optional<DocRequest> getDocRequest(@PathVariable("id_request") Long id_request,  HttpSession session) {
        return docRequestRepo.findById(id_request);
    }

    @GetMapping("/cls_districts")
    public List<ClsDistrict> getListDistricts() {
        return clsDistrictRepo.findAll(Sort.by("id"));
    }

    @GetMapping("/export_to_xlsx")
    public void download(@RequestParam(value = "id_department") Long idDepartment,
                         @RequestParam(value = "status") Integer status,
                         @RequestParam(value = "id_type_request", required = false) Integer idTypeRequest,
                         @RequestParam(value = "id_district", required = false) Integer idDistrict,
                         @RequestParam(value = "innOrName", required = false) String innOrName,
                         HttpServletResponse response) throws IOException {
        DocRequestPrsSearchCriteria searchCriteria = new DocRequestPrsSearchCriteria();
        searchCriteria.setIdDepartment(idDepartment);
        searchCriteria.setStatusReview(status);
        searchCriteria.setIdTypeRequest(idTypeRequest);
        searchCriteria.setIdDistrict(idDistrict);
        searchCriteria.setInnOrName(innOrName);

        List<DocRequest> docRequests = requestService.getRequestsByCriteria(searchCriteria);

        requestService.generateExcel(docRequests, status, response.getOutputStream());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"requests.xlsx\"");
    }
}