package com.czar.bean.userpayments;

import com.czar.bean.Transactions;
import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Data
public class DomainBalance {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private BigInteger depositCount;
  private BigInteger depositSum;
  private BigInteger withdrawCount;
  private BigInteger withdrawSum;
  private String domain;
  private String depositSuccessUrl;
  private String depositFailUrl;
  private String withdrawSuccessUrl;
  private String withdrawFailUrl;
  @OneToOne(mappedBy = "domainBalance")
 // @MapsId
 // @JoinColumn(name = "id")
  private Transactions transactions;


}
