package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getLocalDates(begin, end);
        List<Double> doubleList=new ArrayList<>();//存放每天营业额
        for (LocalDate date : dateList) {
            //查询状态为已完成的营业额
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", begintime);
            map.put("end", endtime);
            map.put("status", Orders.COMPLETED);
          Double d=orderMapper.sunMap(map);
         d=d==null? 0.0 :d;
            doubleList.add(d);
        }
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(doubleList,","))
                .build();
    }
//ctrl+shift+移动
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getLocalDates(begin, end);
        List<Integer> newUserList=new ArrayList<>();//存放新用户
        List<Integer> totalUserList=new ArrayList<>();//存放每天用户
        for (LocalDate date : dateList) {
            //查询状态为已完成的营业额
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endtime);
            Integer totalList=userMapper.countByMap(map);//总
            map.put("begin", begintime);
            Integer newlList=userMapper.countByMap(map);//新增用户
            totalUserList.add(totalList);
            newUserList.add(newlList);
        }
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();

    }

    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getLocalDates(begin, end);
        List<Integer> orderCountList=new ArrayList<>();//存放新用户
        List<Integer> validatList=new ArrayList<>();//存放每天用户
        for (LocalDate date : dateList) {
            //查询状态为已完成的营业额
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endtime);
            map.put("begin", begintime);
            Integer orderCount = orderMapper.countMyMap(map);//每天订单总数
            map.put("status",Orders.COMPLETED);
            Integer valiList = orderMapper.countMyMap(map);//每天有效订单

            orderCountList.add(orderCount);
            validatList.add(valiList);
        }
        Integer orders = orderCountList.stream().reduce(Integer::sum).get();
        Integer total = validatList.stream().reduce(Integer::sum).get();
        //订单完成率
        Double orderComple=0.0;
        if (orders!=0){
            orderComple= total.doubleValue()/orders;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .validOrderCountList(StringUtils.join(validatList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .totalOrderCount(orders)
                .validOrderCount(total)
                .orderCompletionRate(orderComple)
                .build();
    }

    public SalesTop10ReportVO getTopTenStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime begintime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endtime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop = orderMapper.getSalesTop(begintime, endtime);
        List<String> collect = salesTop.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String joined = StringUtils.join(collect, ",");
        List<Integer> numbers = salesTop.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String number = StringUtils.join(numbers, ",");
        return SalesTop10ReportVO.builder()
                .nameList(joined)
                .numberList(number)
                .build();
    }

    public void export(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static List<LocalDate> getLocalDates(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
