/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.gradle.continuous;

import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.service.ServiceRegistry;
import ratpack.gradle.internal.RatpackContinuousRunAction;

import javax.inject.Inject;

public class RatpackContinuousRun extends JavaExec {

  private final String gradleVersion;
  private final String absoluteRootDirPath;

  public RatpackContinuousRun() {
    this.gradleVersion = getProject().getGradle().getGradleVersion();
    this.absoluteRootDirPath = getProject().getRootDir().getAbsolutePath();
  }

  @TaskAction
  @Override
  public void exec() {
    new RatpackContinuousRunAction(gradleVersion, absoluteRootDirPath, this::getServiceRegistry)
      .execute(this);
  }

  @Inject
  protected ServiceRegistry getServiceRegistry() {
    throw new UnsupportedOperationException();
  }

}
