import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouterModule, RouterOutlet} from "@angular/router";
import {i18nProviders} from "src/app/i18n.config";
import {authInterceptor} from "src/app/utils/interceptors/auth.interceptor";

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterOutlet,
    RouterModule
  ],
  providers: [
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([authInterceptor])),
    ...i18nProviders
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
