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

package com.ibm.spark.kernel.protocol.v5.content

import com.ibm.spark.kernel.protocol.v5.KernelMessageContent
import play.api.libs.json.Json

import scala.language.implicitConversions

case class ErrorContent(
  ename: String,
  evalue: String,
  traceback: List[String]
) extends KernelMessageContent{
  override def content : String =
    Json.toJson(this)(ErrorContent.errorContentWrites).toString
}

object ErrorContent {
  implicit val errorContentReads = Json.reads[ErrorContent]
  implicit val errorContentWrites = Json.writes[ErrorContent]

  implicit def ErrorContentToString(errorContent: ErrorContent): String ={
    Json.toJson(errorContent).toString
  }
}