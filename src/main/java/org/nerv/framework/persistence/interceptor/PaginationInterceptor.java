package org.nerv.framework.persistence.interceptor;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.RowBounds;
import org.nerv.framework.persistence.dialect.MySQLDialect;

@Intercepts(
			{@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class })}
		)
public class PaginationInterceptor implements Interceptor {

	public Object intercept(Invocation invocation) throws Throwable {
		
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

		MetaObject metaStatementHandler = MetaObject.forObject(statementHandler);

		RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");

		if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
			return invocation.proceed();
		}

		String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");

		metaStatementHandler.setValue("delegate.boundSql.sql", MySQLDialect.getLimitString(originalSql, rowBounds.getOffset(),rowBounds.getLimit()));

		metaStatementHandler.setValue("delegate.rowBounds.offset",RowBounds.NO_ROW_OFFSET);

		metaStatementHandler.setValue("delegate.rowBounds.limit",RowBounds.NO_ROW_LIMIT);

		Object result = invocation.proceed();
		
		return result;
		
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {

	}

}
