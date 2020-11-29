/*
 * Copyright (c) 2017, Red Hat Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.github.nblxa.turntables;

import java.util.concurrent.atomic.AtomicInteger;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.II_Result;

/**
 * Concurrency test for the SuppVal.eval() method.
 * <p>
 * To run the test:
 * $ ./mvnw clean install -pl turntables-test-concurrency -am
 * $ java -jar turntables-test-concurrency/target/jcstress.jar
 */
@JCStressTest
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE, desc = "Default outcome.")
@Outcome(id = "1, 0", expect = Expect.FORBIDDEN, desc = "Actor1 evaluated the supplier for the 2nd time.")
@Outcome(id = "0, 1", expect = Expect.FORBIDDEN, desc = "Actor2 evaluated the supplier for the 2nd time.")
@Outcome(id = "1, 1", expect = Expect.FORBIDDEN, desc = "Both actors got the result of double evaluation.")
@Outcome(id = "-1, -1", expect = Expect.FORBIDDEN, desc = "Both actors got an unitialized value (null).")
@Outcome(id = "-1, .*", expect = Expect.FORBIDDEN, desc = "Actor1 got an uninitialized value (null).")
@Outcome(id = ".*, -1", expect = Expect.FORBIDDEN, desc = "Actor2 got an uninitialized value (null).")
@State
public class TestSupplierValConcurrency {

  private AtomicInteger atomicInteger = new AtomicInteger(0);
  private TableUtils.SuppVal suppVal = new TableUtils.SuppVal(Typ.INTEGER,
      () -> atomicInteger.getAndAccumulate(1, Integer::sum));

  @Actor
  public void actor1(II_Result r) {
    Object o = suppVal.evaluate();
    if (o == null) {
      r.r1 = -1;
    } else {
      r.r1 = (Integer) o;
    }
  }

  @Actor
  public void actor2(II_Result r) {
    Object o = suppVal.evaluate();
    if (o == null) {
      r.r2 = -1;
    } else {
      r.r2 = (Integer) o;
    }
  }

}
