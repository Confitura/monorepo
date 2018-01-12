import {AppComponent} from './app.component';
import {NgModule} from '@angular/core';
import {PagesModule} from './pages/pages.module';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ProfileModule} from './profile/profile.module';
import {RegistrationModule} from './pages/registration/registration.module';
import {NgLoadingBarModule} from 'ng-loading-bar';
import {HttpClientModule} from '@angular/common/http';
import {AppRoutingModule} from './app-routing.module';
import {CoreModule} from './core/core.module';
import {environment} from '../environments/environment';
import {ServiceWorkerModule} from '@angular/service-worker';


@NgModule({
  imports: [CoreModule,
    PagesModule,
    BrowserModule,
    BrowserAnimationsModule,
    ProfileModule,
    AppRoutingModule,
    RegistrationModule,
    NgLoadingBarModule.forRoot(),
    HttpClientModule,
    ServiceWorkerModule.register('/ngsw-worker.js', {enabled: environment.production})
  ],
  declarations: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule {

}
