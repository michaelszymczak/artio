/*
 * Copyright 2013 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.fix_gateway.dictionary.generation;

import org.junit.Before;
import org.junit.Test;
import uk.co.real_logic.agrona.generation.CompilerUtil;
import uk.co.real_logic.agrona.generation.StringWriterOutputManager;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static uk.co.real_logic.fix_gateway.dictionary.ExampleDictionary.EG_ENUM;
import static uk.co.real_logic.fix_gateway.dictionary.ExampleDictionary.FIELD_EXAMPLE;

public class EnumGeneratorTest
{

    private StringWriterOutputManager outputManager = new StringWriterOutputManager();
    private EnumGenerator enumGenerator = new EnumGenerator(FIELD_EXAMPLE, outputManager);

    @Before
    public void generate()
    {
        enumGenerator.generate();
    }

    @Test
    public void generatesEnumClass() throws Exception
    {
        Class<?> clazz = CompilerUtil.compileInMemory(EG_ENUM, outputManager.getSources());

        assertNotNull("Failed to generate a class", clazz);
        assertTrue("Generated class isn't an enum", clazz.isEnum());

        Enum<?>[] values = (Enum<?>[]) clazz.getEnumConstants();

        assertThat(values, arrayWithSize(2));

        assertEquals("AnEntry", values[0].name());
        assertEquals("AnotherEntry", values[1].name());
    }

    @Test
    public void doesNotGenerateClassForNonEnumFields()
    {
        assertThat(outputManager.getSources(), not(hasKey("EgNotEnum")));
    }

}
