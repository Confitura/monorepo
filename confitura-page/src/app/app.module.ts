import {AppComponent} from './app.component';
import {NgModule} from '@angular/core';
import {PagesModule} from './pages/pages.module';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RegistrationModule} from './pages/registration/registration.module';
import {HttpClientModule} from '@angular/common/http';
import {AppRoutingModule} from './app-routing.module';
import {CoreModule} from './core/core.module';
import {ServiceWorkerModule} from '@angular/service-worker';


@NgModule({
  imports: [
    AppRoutingModule,
    CoreModule,
    PagesModule,
    BrowserModule,
    BrowserAnimationsModule,
    RegistrationModule,
    HttpClientModule,
    ServiceWorkerModule.register('/ngsw-worker.js', {enabled: false})
  ],
  declarations: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule {

}
