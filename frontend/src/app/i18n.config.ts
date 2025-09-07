// src/app/i18n.config.ts
import { HttpClient } from '@angular/common/http';
import { APP_INITIALIZER, Provider } from '@angular/core';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { lastValueFrom } from 'rxjs';

// Simple, stable HTTP loader (no external package)
class SimpleHttpLoader implements TranslateLoader {
  constructor(
    private http: HttpClient,
    private prefix: string = '/assets/i18n/',
    private suffix: string = '.json'
  ) {}
  getTranslation(lang: string) {
    return this.http.get<Record<string, any>>(`${this.prefix}${lang}${this.suffix}`);
  }
}

// Preload a key so UI renders with translations immediately
function initTranslateFactory(translate: TranslateService) {
  return async () => {
    const browser = (navigator.language || 'en').split('-')[0];
    const saved = localStorage.getItem('lang') || browser;
    translate.addLangs(['en', 'pl']);
    translate.setDefaultLang('en');
    translate.use(['en', 'pl'].includes(saved) ? saved : 'en');
    await lastValueFrom(translate.get('nav.dashboard'));
  };
}

export const i18nProviders: Provider[] = [
  TranslateModule.forRoot({
    loader: {
      provide: TranslateLoader,
      useClass: SimpleHttpLoader,
      deps: [HttpClient],
    },
    defaultLanguage: 'en',
    isolate: false,
  }).providers!,
  {
    provide: APP_INITIALIZER,
    useFactory: initTranslateFactory,
    deps: [TranslateService],
    multi: true,
  },
];
