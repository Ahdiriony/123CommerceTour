package concerttours.interceptors;

import concerttours.events.BandAlbumSalesEvent;
import concerttours.model.BandModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import static de.hybris.platform.servicelayer.model.ModelContextUtils.getItemModelContext;


public class BandAlbumSalesInterceptor implements PrepareInterceptor<BandModel>, ValidateInterceptor<BandModel>
{
	private static final long BIG_SALES = 50000L;
	private static final long NEGATIVE_SALES = 0L;

	@Autowired
	private EventService eventService;

	@Override
	public void onPrepare(BandModel bandModel, InterceptorContext interceptorContext) throws InterceptorException
	{
		final Long sales = bandModel.getAlbumSales();
		if (sales != null && sales.longValue() < NEGATIVE_SALES)
		{
			throw new InterceptorException("Album sales must be positive");
		}
	}

	@Override
	public void onValidate(BandModel bandModel, InterceptorContext interceptorContext)
	{
		if (hasBecomeBig(bandModel, interceptorContext))
		{
			eventService.publishEvent(new BandAlbumSalesEvent(bandModel.getCode(), bandModel.getName(), bandModel.getAlbumSales()));
		}
	}

	private boolean hasBecomeBig(final BandModel band, final InterceptorContext ctx)
	{
		final Long sales = band.getAlbumSales();
		if (sales != null && sales.longValue() >= BIG_SALES)
		{
			if (ctx.isNew(band))
			{
				return true;
			}
			else
			{
				final Long oldValue = getItemModelContext(band).getOriginalValue(BandModel.ALBUMSALES);
				return oldValue == null || oldValue.intValue() < BIG_SALES;
			}
		}
		return false;
	}
}
