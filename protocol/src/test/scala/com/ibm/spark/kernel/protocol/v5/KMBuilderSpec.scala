/*
 * Copyright 2014 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.spark.kernel.protocol.v5

import com.ibm.spark.kernel.protocol.v5.content.StreamContent
import org.scalatest.{Matchers, FunSpec}
import play.api.libs.json._

class KMBuilderSpec extends FunSpec with Matchers {
  describe("KMBuilder") {
    val emptyKM = KernelMessage(
      ids          = Seq(),
      signature    = "",
      header       = HeaderBuilder.empty,
      parentHeader = HeaderBuilder.empty,
      metadata     = Metadata(),
      contentString = ""
    )
    val nonEmptyHeader = Header("1", "user", "2", "msg", "version")

    describe("constructor") {
      it("should hold an empty KernelMessage when constructed by default") {
        KMBuilder().km should be(emptyKM)
      }

      it("should throw an IllegalArgumentException if given a null message") {
        intercept[IllegalArgumentException] {
          KMBuilder(null)
        }
      }
    }

    describe("#build"){
      it("should build a KernelMessage") {
        KMBuilder().build.copy(metadata = Metadata()) should be(emptyKM)
      }

      it("should include default metadata in built message ") {
        class KM2 extends KMBuilder {
          override def metadataDefaults : Metadata = {
            Metadata("foo" -> "bar", "baos" -> "bean")
          }
        }
        val builder = new KM2
        val metadata = builder.build.metadata
        builder.metadataDefaults.foreach { case (k, v) =>
            assert (metadata.contains(k) && metadata(k) == v)
        }
      }
    }

    describe("withXYZ"){
      describe("#withIds"){
        it("should produce a KMBuilder with a KernelMessage with ids set") {
          val ids = Seq("baos", "win")
          val builder = KMBuilder().withIds(ids)
          builder.km.ids should be (ids)
        }
      }

      describe("#withSignature"){
        it("should produce a KMBuilder with a KernelMessage with signature set") {
          val sig = "beans"
          val builder = KMBuilder().withSignature(sig)
          builder.km.signature should be (sig)
        }
      }

      describe("#withHeader"){
        it("should produce a KMBuilder with a KernelMessage with header set," +
           "given a Header") {
          val builder = KMBuilder().withHeader(nonEmptyHeader)
          builder.km.header should be (nonEmptyHeader)
        }
        it("should produce a KMBuilder with a KernelMessage with header set " +
          "to a header for the given message type") {
          val msgType = MessageType.ExecuteResult
          val header = HeaderBuilder.create(msgType.toString).copy(msg_id = "")
          val builder = KMBuilder().withHeader(msgType)
          builder.km.header.copy(msg_id = "") should be (header)
        }
      }

      describe("#withParent"){
        it("should produce a KMBuilder with a KernelMessage with " +
           "parentHeader set to the header of the given parent message") {
          val parent = emptyKM.copy(header = nonEmptyHeader)
          val builder = KMBuilder().withParent(parent)
          builder.km.parentHeader should be (parent.header)
        }
      }

      describe("#withParentHeader"){
        it("should produce a KMBuilder with a KernelMessage with " +
           "parentHeader set") {
          val builder = KMBuilder().withParentHeader(nonEmptyHeader)
          builder.km.parentHeader should be (nonEmptyHeader)
        }
      }

      describe("#withMetadata"){
        it("should produce a KMBuilder with a KernelMessage whose metadata " +
           "contains the given metadata") {
          val metadata = Metadata("foo" -> "bar", "baos" -> "bean")
          val builder = KMBuilder().withMetadata(metadata)
          builder.km.metadata should be (metadata)
          val builtKM = builder.build
          metadata.foreach { case (k, v) =>
            assert (builtKM.metadata.contains(k) && builtKM.metadata(k) == v)
          }
        }
      }

      describe("#withContentString"){
        it("should produce a KMBuilder with a KernelMessage with content set") {
          val content = "foo bar"
          val builder = KMBuilder().withContentString(content)
          builder.km.contentString should be (content)
        }
        it("should produce a KMBuilder with a KernelMessage with content" +
           "containing a JSON string of the given object") {
          val sc = StreamContent("foo", "bar")
          val builder = KMBuilder().withContentString(sc)
          builder.km.contentString should be (Json.toJson(sc).toString)
        }
      }
    }
  }
}
