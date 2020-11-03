package concerttours.events;

import concerttours.model.NewsModel;
import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.PublishEventContext;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;


public class BandAlbumSalesEventListener extends AbstractEventListener<BandAlbumSalesEvent> implements ClusterAwareEvent
{
	private static final String BAND_SALES_HEADLINE = "%s album sales exceed 50000";
	private static final String BAND_SALES_CONTENT = "%s album sales reported as %d";
	private ModelService modelService;

	@Override
	protected void onEvent(BandAlbumSalesEvent event)
	{
		if (event != null)
		{
			final String headline = String.format(BAND_SALES_HEADLINE, event.getName());
			final String content = String.format(BAND_SALES_CONTENT, event.getName(), event.getSales());
			final NewsModel news = modelService.create(NewsModel.class);
			news.setDate(new Date());
			news.setContent(content);
			news.setHeadline(headline);
			modelService.save(news);
		}
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	public boolean canPublish(PublishEventContext publishEventContext)
	{
		return publishEventContext.getSourceNodeId() == publishEventContext.getTargetNodeId();
	}
}
