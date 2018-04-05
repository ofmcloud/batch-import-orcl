package com.onfacemind.batchimportorcl;

import org.junit.Test;

public class TestInteger {

    @Test
    public void testInt(){

        int total = 10000;

        int pageSize = 1000;

        int pageCount = (total/pageSize) + ((total%pageSize) == 0?0:1);

        System.out.println(pageCount);

    }

}
