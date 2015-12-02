package com.grachro.muddler;

import java.io.File;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class View {

	private static String getPath() {
		return Muddler.ROOT_PATH + "/script";
	}

	public static String edit(Object model, String templetePath) {
		StringWriter writer = new StringWriter();
		String out;
		try {
			String root = getPath();
			MustacheFactory mustacheFactory = new DefaultMustacheFactory(new File(root));
			Mustache mustache = mustacheFactory.compile(templetePath);
			mustache.execute(writer, model).flush();

			writer.flush();
			out = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			out = editException(e);
		} finally {
			IOUtils.closeQuietly(writer);
		}

		return out;
	}

	private static String editException(Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("</pre>").append(ExceptionUtils.getStackTrace(e)).append("</pre>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();

	}
}
