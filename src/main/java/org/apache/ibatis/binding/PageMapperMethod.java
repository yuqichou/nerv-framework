/*
 *    Copyright 2009-2012 The MyBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.binding;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.nerv.framework.persistence.annotation.Pagination;
import org.nerv.framework.util.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageMapperMethod {

  private final SqlSession sqlSession;
  private final Configuration config;

  private SqlCommandType type;
  private String commandName;

  private Class<?> declaringInterface;
  private Method method;


  public PageMapperMethod(Class<?> declaringInterface, Method method, SqlSession sqlSession) {
    this.sqlSession = sqlSession;
    this.method = method;
    this.config = sqlSession.getConfiguration();
    //this.hasNamedParameters = false;
    this.declaringInterface = declaringInterface;
    setupFields();
    setupCommandType();
    validateStatement();
  }

  public Object execute(Object[] args) {
     if (SqlCommandType.SELECT == type) {
    	 Pagination pagination=method.getAnnotation(Pagination.class);
    	 
    	 if(pagination==null){
    		 throw new BindingException("annotation Pagination not found");
    	 }
    	 
         String commandCountName=declaringInterface.getName()+"."+pagination.countCommand();
         if(commandCountName==null || commandCountName.trim().length()<1){
        	 throw new BindingException("commandCountName is null");
         }
         
         Pageable pageRequest = (Pageable) getPageRequestParam(args);
         Object param = getSelectParam(args);
         
         // select
         List<Object> resultList = sqlSession.selectList(commandName,
        		 										 param,
        		 										 new RowBounds(pageRequest.getOffset(), pageRequest.getPageSize()));
          
         //执行count
         Object countResult = sqlSession.selectOne(commandCountName, param);
         
         return new Page(pageRequest.getPageNumber(),Integer.valueOf(countResult.toString()), pageRequest.getPageSize(), resultList);
     } else {
    	 throw new BindingException("current command " +type+" not supported");
    }
  }




  
  private Object getSelectParam(Object[] args){
	  for (int i = 0; i < args.length; i++) {
		if(args[i]!=null && !(args[i] instanceof PageRequest)){
			return args[i];
		}
	  }
	  return null;
  }
  
  private Object getPageRequestParam(Object[] args){
	  for (int i = 0; i < args.length; i++) {
		if(args[i] instanceof Pageable){
			return args[i];
		}
	  }
	  return null;
  }
  

  // Setup //

  private void setupFields() {
    this.commandName = declaringInterface.getName() + "." + method.getName();
  }


  private void setupCommandType() {
    MappedStatement ms = config.getMappedStatement(commandName);
    type = ms.getSqlCommandType();
    if (type == SqlCommandType.UNKNOWN) {
      throw new BindingException("Unknown execution method for: " + commandName);
    }
  }

  private void validateStatement() {
    try {
      config.getMappedStatement(commandName);
    } catch (Exception e) {
      throw new BindingException("Invalid bound statement (not found): " + commandName, e);
    }
  }

  public static class MapperParamMap<V> extends HashMap<String, V> {

    private static final long serialVersionUID = -2212268410512043556L;

    @Override
    public V get(Object key) {
      if (!super.containsKey(key)) {
        throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + this.keySet());
      }
      return super.get(key);
    }

  }
  
  
  
}
