package ohtu.Kauppa;

import ohtu.verkkokauppa.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class VerkkokauppaTest {
    
    @Test
    public void ostoksenPaatyttyaPankinMetodiaTilisiirtoKutsutaan() {
        
        Pankki pankki = mock(Pankki.class);
        Viitegeneraattori viitegeneraattori = mock(Viitegeneraattori.class);
        
        when(viitegeneraattori.uusi()).thenReturn(42);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "kalja", 5));
        
        Kauppa k = new Kauppa(varasto, pankki, viitegeneraattori);
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("Sami", "12345");
        
        verify(pankki).tilisiirto(eq("Sami"), eq(42), eq("12345"), eq("33333-44455"), eq(5));
    }
    
    @Test
    public void ostetaanKaksiEriTuotettaJaPaadytaanTilisiirtoKutsuun() {
        
        Pankki pankki = mock(Pankki.class);
        Viitegeneraattori viitegeneraattori = mock(Viitegeneraattori.class);
        
        when(viitegeneraattori.uusi()).thenReturn(1000);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "kalja", 5));
        
        when(varasto.saldo(2)).thenReturn(12);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "jallu", 11));
        
        Kauppa k = new Kauppa(varasto, pankki, viitegeneraattori);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("Topi", "2345");
        
        verify(pankki).tilisiirto(eq("Topi"), eq(1000), eq("2345"), eq("33333-44455"), eq(16));
    }
    
    @Test
    public void ostetaanKaksiSamaaTuotettaJaPaadytaanTilisiirtoKutsuun() {
        
        Pankki pankki = mock(Pankki.class);
        Viitegeneraattori viitegeneraattori = mock(Viitegeneraattori.class);
        
        when(viitegeneraattori.uusi()).thenReturn(1000);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "kalja", 5));
        Kauppa k = new Kauppa(varasto, pankki, viitegeneraattori);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.tilimaksu("Topi", "2345");
        
        verify(pankki).tilisiirto(eq("Topi"), eq(1000), eq("2345"), eq("33333-44455"), eq(10));
    }
    
    @Test
    public void ostetaanKaksiTuotettaJoistaToinenLoppuJaSuoritetaanOstos() {
        Pankki pankki = mock(Pankki.class);
        Viitegeneraattori viitegeneraattori = mock(Viitegeneraattori.class);
        
        when(viitegeneraattori.uusi()).thenReturn(123);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(1)).thenReturn(1);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "kalja", 5));
        when(varasto.saldo(2)).thenReturn(0);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "maito", 2));
        
        Kauppa k = new Kauppa(varasto, pankki, viitegeneraattori);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("Sami", "12345");
        
        verify(pankki).tilisiirto(eq("Sami"), eq(123), eq("12345"), eq("33333-44455"), eq(5));
    }
    
    @Test
    public void aloitaAsiointiLuoUudenOstoskortin() {
        
        Pankki pankki = mock(Pankki.class);
        Viitegeneraattori viitegeneraattori = mock(Viitegeneraattori.class);
        
        when(viitegeneraattori.uusi()).thenReturn(1000);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "kalja", 5));
        Kauppa k = new Kauppa(varasto, pankki, viitegeneraattori);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.tilimaksu("Topi", "2345");
        
        verify(pankki, times(1)).tilisiirto(anyString(), anyInt(), anyString(), anyString(), anyInt());
        
        k.aloitaAsiointi();        
        k.lisaaKoriin(1);
        k.tilimaksu("Maaret", "11111");
        verify(pankki, times(2)).tilisiirto(anyString(), anyInt(), anyString(), anyString(), anyInt());
    }
    
    @Test
    public void kauppaPyytääUUdenViitenumeronJokaiselleMaksutapahtumalle() {
        Pankki pankki = mock(Pankki.class);
        Viitegeneraattori viitegeneraattori = mock(Viitegeneraattori.class);
        
        when(viitegeneraattori.uusi()).thenReturn(1)
                .thenReturn(2).thenReturn(3);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "kalja", 5));
        
        when(varasto.saldo(2)).thenReturn(12);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "jallu", 11));
        
        Kauppa k = new Kauppa(varasto, pankki, viitegeneraattori);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("Maaret", "1111");
        verify(viitegeneraattori, times(1)).uusi();
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("Maaret", "1111");
        verify(viitegeneraattori, times(2)).uusi();
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("Maaret", "1111");
        verify(viitegeneraattori, times(3)).uusi();
    }
    
    @Test
    public void poistoOstoskoristaOnnistuu() {
        
        Pankki pankki = mock(Pankki.class);
        Viitegeneraattori viitegeneraattori = mock(Viitegeneraattori.class);
        
        when(viitegeneraattori.uusi()).thenReturn(1);
        
        Varasto varasto = mock(Varasto.class);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "kalja", 5));
        
        when(varasto.saldo(2)).thenReturn(12);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "jallu", 11));
        
        Kauppa k = new Kauppa(varasto, pankki, viitegeneraattori);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.poistaKorista(2);
        
        k.lisaaKoriin(1);
        
        k.tilimaksu("Matti", "5555-5555");
        
        verify(pankki).tilisiirto(eq("Matti"), eq(1), eq("5555-5555"), eq("33333-44455"), eq(16));
        
    }
    
}
