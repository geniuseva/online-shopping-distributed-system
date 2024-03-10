package com.yin.onlineshopping.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class HelloControllerTest {
    @Resource
    HelloController helloController;

    @Mock
    DependencyA mock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void helloWorld_actual() {
        helloController = new HelloController(new DependencyA());
        String res = helloController.helloWorld();
        assertEquals("Hello world", res);
    }
    @Test
    void helloWorld_mock() {
        helloController = new HelloController(mock);
        when(mock.send(any()))
                .thenReturn("abcd");
        String res = helloController.helloWorld();
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(mock).send(argument.capture());
        assertEquals("Hello world", argument.getValue());
        assertEquals("abcd", res);
    }

    @Test
    void helloMockStaticTest() {
        MockedStatic<StaticDependency> staticDependency = Mockito.mockStatic(StaticDependency.class);
        staticDependency.when(() -> StaticDependency.staticSend(any()))
                .thenReturn("ABC");
        helloController = new HelloController(mock);
        String res = helloController.staticHello();
        assertEquals("ABC", res);
    }
    @Test
    void testHelloWorld() {
        helloController = new HelloController(new DependencyA());
        String res = helloController.echo("ABC");
        assertEquals("You just Input :ABC", res);
    }
}