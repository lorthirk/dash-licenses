/*************************************************************************
 * Copyright (c) 2020, The Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License 2.0 which accompanies this 
 * distribution, and is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *************************************************************************/
package org.eclipse.dash.licenses.spdx;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * Simple SPDX License expression parser.
 */
public class SpdxExpressionParser {
	// TODO Add rudimentary error recovery
	public SpdxExpression parse(String expression) {
		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(expression));
		tokenizer.ordinaryChar('(');
		tokenizer.ordinaryChar(')');

		return parse(tokenizer);
	}

	private SpdxExpression parse(StreamTokenizer tokenizer) {
		try {
			SpdxExpression expression = null;
			while (true) {
				int token = tokenizer.nextToken();
				switch (token) {
				case StreamTokenizer.TT_EOF:
					return expression;
				case StreamTokenizer.TT_WORD:
					String symbol = tokenizer.sval.toLowerCase();
					switch (symbol) {
					case "and": {
						SpdxExpression right = parse(tokenizer);
						expression = new SpdxConjunction(expression, right);
						break;
					}
					case "or": {
						SpdxExpression right = parse(tokenizer);
						expression = new SpdxDisjunction(expression, right);
						break;
					}
					case "with": {
						SpdxExpression right = parse(tokenizer);
						expression = new SpdxException(expression, right);
						break;
					}
					default:
						// Assume that the token is an SPDX identifier
						// Note that we grab the original form from the tokenizer
						// and note the converted (lowercase) version that we used
						// in the switch.
						expression = new SpdxIdentifier(tokenizer.sval);
					}
					break;
				case '(':
					expression = new SpdxGroup(parse(tokenizer));
					break;
				case ')':
					return expression;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}