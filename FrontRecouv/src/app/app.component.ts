import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { AppInitService } from './services/app-init.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  isCollapsed: boolean = false;
  ishidden: boolean = false;

  constructor(private router: Router, 
              private authService: AuthService,
              private appInitService: AppInitService
            ) {}

  ngOnInit(): void {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateActiveState();
      }
    });
    this.updateActiveState();
    this.appInitService.initClientsWithRelance();
  }

  updateActiveState(): void {
    const currentRoute = this.router.url;
    const isAuthRoute = currentRoute.includes('login') ;

    this.ishidden = !this.authService.isLoggedIn() || isAuthRoute;

  }

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }
}
