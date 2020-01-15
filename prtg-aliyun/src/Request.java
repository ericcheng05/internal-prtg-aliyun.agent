import util.StringUtil;

/**
 * @author Eric
 *
 */
public class Request
{
	private String regionId = null;
	private String project = null;
	private String metric = null;
	private String period = "300"; // 300
	private String accessKeyId = null;
	private String accessKeySecret = null;
	private String dimensions = null;
	private StringUtil.AliyunProduct product = null;
	
	public Request(String[] args)
	{
		this.setRegionId(args[0]);
		this.setProject(args[1]);
		this.setMetric(args[2]);
		this.setAccessKeyId(args[3]);
		this.setAccessKeySecret(args[4]);
		this.setPeriod(args[5]);
			
		if (args.length == 7)
		{
			this.setDimensions("{\"instanceId\":\"" + args[6] + "\"}");
		}
		else if (args.length == 9)
		{
			this.setDimensions("{\"instanceId\":\"" + args[6] + "\",\"" + args[7] + "\":\"" + args[8] + "\"}");							
		}			
		else if (args.length == 11)
		{
			this.setDimensions("{\"instanceId\":\"" + args[6] + "\",\"" + args[7] + "\":\"" + args[8] + "\",\"" + args[9] + "\":\"" + args[10] + "\"}");				
		}
		
		this.setProduct(StringUtil.parseProduct(this.getProject()));
	}

	/**
	 * @return the regionId
	 */
	public String getRegionId()
	{
		return regionId;
	}

	/**
	 * @param regionId the regionId to set
	 */
	public void setRegionId(String regionId)
	{
		this.regionId = regionId;
	}

	/**
	 * @return the project
	 */
	public String getProject()
	{
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(String project)
	{
		this.project = project;
	}

	/**
	 * @return the metric
	 */
	public String getMetric()
	{
		return metric;
	}

	/**
	 * @param metric the metric to set
	 */
	public void setMetric(String metric)
	{
		this.metric = metric;
	}

	/**
	 * @return the period
	 */
	public String getPeriod()
	{
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period)
	{
		this.period = period;
	}

	/**
	 * @return the accessKeyId
	 */
	public String getAccessKeyId()
	{
		return accessKeyId;
	}

	/**
	 * @param accessKeyId the accessKeyId to set
	 */
	public void setAccessKeyId(String accessKeyId)
	{
		this.accessKeyId = accessKeyId;
	}

	/**
	 * @return the accessKeySecret
	 */
	public String getAccessKeySecret()
	{
		return accessKeySecret;
	}

	/**
	 * @param accessKeySecret the accessKeySecret to set
	 */
	public void setAccessKeySecret(String accessKeySecret)
	{
		this.accessKeySecret = accessKeySecret;
	}

	/**
	 * @return the dimensions
	 */
	public String getDimensions()
	{
		return dimensions;
	}

	/**
	 * @param dimensions the dimensions to set
	 */
	public void setDimensions(String dimensions)
	{
		this.dimensions = dimensions;
	}

	/**
	 * @return the product
	 */
	public StringUtil.AliyunProduct getProduct()
	{
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(StringUtil.AliyunProduct product)
	{
		this.product = product;
	}	
}
