package com.example.saxonheritage.services;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface MemberService {
    @WebMethod
    String registerMember(String name, String email) throws GlobalCustomException;

    @WebMethod
    String updateMemberDetails(String memberId, String email);

    @WebMethod
    String getMembershipInfo(String memberId);

    @WebMethod
    void submitVisitorReport(String memberId, List<String> visitorReports) throws GlobalCustomException;

    @WebMethod
    List<String> getVisitorReports(String memberId) throws GlobalCustomException;

    @WebMethod
    void sendPromotion(String memberId, String promotion) throws GlobalCustomException;

    @WebMethod
    List<String> getPromotions(String memberId) throws GlobalCustomException;
}
