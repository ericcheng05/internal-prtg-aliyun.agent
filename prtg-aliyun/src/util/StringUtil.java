package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class StringUtil
{
	public static enum AliyunProduct
	{
		ECS, RDS, ALB, SLB, OSS, NAS, Memcache, EIP, Redis, MessageService, CDN, AnalyticDB, MongoDB,
		ExpressConnect, FunctionCompute, NATGateway, LogService, ContainerService, VPNGateway,
		SharedBandwidthPackage, CEN, EdgeNodeService, OpenSearch, SecureAcceleration, GlobalAcceleration, PhysicalConnection 
	}
	
	public static enum StatisticType
	{
		Average, AverageMaxMin, Value_Upper, Value_Lower
	}
	
	public static String formatISO8601Date (Date date)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		df.setTimeZone(new SimpleTimeZone(0, "GMT"));
		return df.format(date);
	}
	
	public static StatisticType parseStatistic (String result)
	{
		StatisticType type = null;
		
		if (result.contains("Maximum") && result.contains("Minimum") && result.contains("Average"))
		{
			type = StatisticType.AverageMaxMin;
		}
		else if (result.contains("Average"))
		{
			type = StatisticType.Average;
		}
		else if (result.contains("Value"))
		{
			type = StatisticType.Value_Upper;
		}
		else if (result.contains("value"))
		{
			type = StatisticType.Value_Lower;
		}
		
		return type;
	}
	
	public static AliyunProduct parseProduct (String project)
	{
		AliyunProduct product = null;
				
		if (project.equals("acs_ecs_dashboard"))
		{
			product = AliyunProduct.ECS;
		}
		else if (project.equals("acs_rds_dashboard"))
		{
			product = AliyunProduct.RDS;
		}
		else if (project.equals("acs_alb"))
		{
			product = AliyunProduct.ALB;
		}
		else if (project.equals("acs_slb_dashboard"))
		{
			product = AliyunProduct.SLB;
		}
		else if (project.equals("acs_oss"))
		{
			product = AliyunProduct.OSS;
		}
		else if (project.equals("acs_nas"))
		{
			product = AliyunProduct.NAS;
		}
		else if (project.equals("acs_memcache"))
		{
			product = AliyunProduct.Memcache;
		}
		else if (project.equals("acs_vpc_eip"))
		{
			product = AliyunProduct.EIP;
		}
		else if (project.equals("acs_kvstore"))
		{
			product = AliyunProduct.Redis;
		}
		else if (project.equals("acs_mns_new"))
		{
			product = AliyunProduct.MessageService;
		}
		else if (project.equals("acs_cdn"))
		{
			product = AliyunProduct.CDN;
		}
		else if (project.equals("acs_ads"))
		{
			product = AliyunProduct.AnalyticDB;
		}
		else if (project.equals("acs_mongodb"))
		{
			product = AliyunProduct.MongoDB;
		}
		else if (project.equals("acs_express_connect"))
		{
			product = AliyunProduct.ExpressConnect;				
		}
		else if (project.equals("acs_fc"))
		{
			product = AliyunProduct.FunctionCompute;
		}
		else if (project.equals("acs_nat_gateway"))
		{
			product = AliyunProduct.NATGateway;
		}
		else if (project.equals("acs_sls_dashboard"))
		{
			product = AliyunProduct.LogService;
		}
		else if (project.equals("acs_containerservice_dashboard"))
		{
			product = AliyunProduct.ContainerService;
		}
		else if (project.equals("acs_vpn"))
		{
			product = AliyunProduct.VPNGateway;
		}
		else if (project.equals("acs_bandwidth_package"))
		{
			product = AliyunProduct.SharedBandwidthPackage;
		}
		else if (project.equals("acs_cen"))
		{
			product = AliyunProduct.CEN;
		}
		else if (project.equals("acs_ens"))
		{
			product = AliyunProduct.EdgeNodeService;
		}
		else if (project.equals("acs_opensearch"))
		{
			product = AliyunProduct.OpenSearch;
		}
		else if (project.equals("acs_scdn"))
		{
			product = AliyunProduct.SecureAcceleration;
		}
		else if (project.equals("acs_global_acceleration"))
		{
			product = AliyunProduct.GlobalAcceleration;
		}
		else if (project.equals("acs_physical_connection"))
		{
			product = AliyunProduct.PhysicalConnection;
		}
		else
		{
			return null;
		}
		
		return product;
	}
}
