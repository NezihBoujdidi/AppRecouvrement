import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LogInComponent } from './log-in/log-in.component';
import { HomePageComponent } from './home-page/home-page.component';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from './sidebar/sidebar.component';
import { AfaireComponent } from './afaire/afaire.component';
import { BalanceAgeeComponent } from './balance-agee/balance-agee.component';
import { ProfilClientComponent } from './profil-client/profil-client.component';
import { DetailsPaiementComponent } from './details-paiement/details-paiement.component';

@NgModule({
  declarations: [
    AppComponent,
    LogInComponent,
    HomePageComponent,
    SidebarComponent,
    AfaireComponent,
    BalanceAgeeComponent,
    ProfilClientComponent,
    DetailsPaiementComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }